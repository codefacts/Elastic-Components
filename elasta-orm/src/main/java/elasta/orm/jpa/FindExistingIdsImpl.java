package elasta.orm.jpa;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.jpa.models.ChildModelInfo;
import elasta.orm.jpa.models.ModelInfo;
import elasta.orm.jpa.models.PropInfo;
import elasta.orm.jpa.models.RelationInfo;
import elasta.orm.json.core.RelationType;
import elasta.orm.json.sql.DbSql;
import elasta.orm.json.sql.SqlAndParams;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Shahadat on 10/6/2016.
 */
public class FindExistingIdsImpl implements FindExistingIds {
    private final String COMMA = ", ";
    private final ModelInfoProvider modelInfoProvider;
    private final DbSql dbSql;

    public FindExistingIdsImpl(ModelInfoProvider modelInfoProvider, DbSql dbSql) {
        this.modelInfoProvider = modelInfoProvider;
        this.dbSql = dbSql;
    }

    public Promise<TableIdPairs> findExistingTableIds(String model, JsonObject data) {

        Fineder finder = new Fineder();

        return Promises.when(
            finder.queryList(model, data).stream().map(sqlAndParams -> dbSql.query(sqlAndParams.getSql(), sqlAndParams.getParams())).collect(Collectors.toList())
        ).map(resultSets -> {
            ImmutableSet.Builder<TableIdPair> tableIdPairBuilder = ImmutableSet.builder();
            ImmutableSet.Builder<RelationTableIdPair> relationTableIdPairBuilder = ImmutableSet.builder();

            Iterator<ResultSet> iterator = resultSets.iterator();

            finder.tablePrimaryKeySet.build().forEach(tablePrimary -> {

                iterator.next().getResults().forEach(jsonArray -> {

                    tableIdPairBuilder.add(new TableIdPairBuilder()
                        .setTable(tablePrimary.getTable())
                        .setId(jsonArray.getValue(0))
                        .createTableIdPair());
                });
            });

            finder.relationTablePrimaryKeySet.build().forEach(relationTablePrimary -> {

                iterator.next().getResults().forEach(jsonArray -> relationTableIdPairBuilder.add(
                    new RelationTableIdPair(relationTablePrimary.getRelationTable(), jsonArray.getValue(0), jsonArray.getValue(1))
                ));
            });

            return new TableIdPairs(tableIdPairBuilder.build(), relationTableIdPairBuilder.build());

        });
    }

    private class Fineder {
        private final java.lang.CharSequence _OR_ = " or ";
        final ImmutableSet.Builder<TablePrimary> tablePrimaryKeySet = ImmutableSet.builder();
        final ImmutableSet.Builder<RelationTablePrimary> relationTablePrimaryKeySet = ImmutableSet.builder();
        final ImmutableSet.Builder<String> tablesBuilder = ImmutableSet.builder();
        final ImmutableSet.Builder<String> relationTablesBuilder = ImmutableSet.builder();
        final ImmutableSetMultimap.Builder<String, Object> tableIdSetBuilder = ImmutableSetMultimap.builder();
        final ImmutableSetMultimap.Builder<String, RelationColumnValuePairs> relationTableIdSetBuilder = ImmutableSetMultimap.builder();

        public List<SqlAndParams> queryList(String model, JsonObject data) {

            ModelInfo modelInfo = modelInfoProvider.get(model);

            tablePrimaryKeySet.add(new TablePrimaryBuilder()
                .setTable(modelInfo.getTable())
                .setPrimaryKey(modelInfo.getPrimaryKey())
                .createTablePrimary());
            tablesBuilder.add(modelInfo.getTable());

            Object idValue = data.getValue(modelInfo.getPrimaryKey());

            if (idValue == null) {
                throw new OrmException("Entity Object does not containsTable primary key value. Primary key '" + modelInfo.getPrimaryKey() + "' = " + idValue);
            }

            tableIdSetBuilder.put(modelInfo.getTable(), idValue);

            mergeRecursive(modelInfo, data, tablePrimaryKeySet, tablesBuilder, tableIdSetBuilder);

            ImmutableSetMultimap<String, Object> tableIdSet = tableIdSetBuilder.build();
            ImmutableSetMultimap<String, RelationColumnValuePairs> relationTableIdSet = relationTableIdSetBuilder.build();

            ImmutableSet<TablePrimary> tablePrimaries = tablePrimaryKeySet.build();
            ImmutableSet<RelationTablePrimary> relationTablePrimaries = relationTablePrimaryKeySet.build();

            ImmutableList.Builder<SqlAndParams> paramsListBuilder = ImmutableList.builder();

            tablePrimaries.forEach(tablePrimary -> {

                JsonArray params = new JsonArray(ImmutableList.copyOf(tableIdSet.get(tablePrimary.getTable())));

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0, paramsSize = params.size(); i < paramsSize; i++) {
                    stringBuilder.append("?").append(COMMA);
                }

                if (params.size() > 0) {
                    stringBuilder.delete(stringBuilder.length() - COMMA.length(), stringBuilder.length());
                }

                String tablePrimaryKey = tablePrimary.getTable() + "." + tablePrimary.getPrimaryKey();

                String sql = "select " + tablePrimaryKey + " from " + tablePrimary.getTable() + " where " + tablePrimaryKey + " in (" +
                    stringBuilder.toString() + ")";

                paramsListBuilder.add(new SqlAndParams(sql, params));
            });

            relationTablePrimaries.forEach(relationTablePrimary -> {

                ImmutableList.Builder<Object> paramsBuilder = ImmutableList.builder();

                relationTableIdSet
                    .get(relationTablePrimary.getRelationTable())
                    .stream().flatMap(relationColumnValuePairs -> Stream.of(relationColumnValuePairs.getLeftColumnValue(), relationColumnValuePairs.getRightColumnValue()))
                    .forEach(paramsBuilder::add);

                JsonArray params = new JsonArray(paramsBuilder.build());

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0, size = relationTableIdSet.size(); i < size; i++) {
                    stringBuilder.append(

                        "(" + relationTablePrimary.getRelationTable() + "." + relationTablePrimary.getLeftColumn() +
                            " = ? and " + relationTablePrimary.getRelationTable() + "." + relationTablePrimary.getRightColumn() + " = ?" + ")"

                    ).append(_OR_);
                }

                if (params.size() > 0) {
                    stringBuilder.delete(stringBuilder.length() - _OR_.length(), stringBuilder.length());
                }

                String sql = "select " +
                    relationTablePrimary.getRelationTable() + "." + relationTablePrimary.getLeftColumn() + ", " +
                    relationTablePrimary.getRelationTable() + "." + relationTablePrimary.getRightColumn() +
                    " from " + relationTablePrimary.getRelationTable() + " where " + stringBuilder.toString();

                paramsListBuilder.add(new SqlAndParams(sql, params));
            });

            return paramsListBuilder.build();
        }

        private void mergeRecursive(ModelInfo primaryModelInfo, JsonObject data,
                                    ImmutableSet.Builder<TablePrimary> tablePrimaryKeySet,
                                    ImmutableSet.Builder<String> tables, ImmutableSetMultimap.Builder<String, Object> tableIdSetBuilder) {

            for (Map.Entry<String, PropInfo> entry : primaryModelInfo.getPropInfoMap().entrySet()) {
                if (entry.getValue().hasRelation()) {
                    RelationInfo relationInfo = entry.getValue().getRelationInfo();

                    ChildModelInfo joinModelInfo = relationInfo.getJoinModelInfo();

                    ModelInfo childModelInfo = modelInfoProvider.get(joinModelInfo.getChildModel());

                    if (relationInfo.getRelationType() == RelationType.ONE_TO_MANY || relationInfo.getRelationType() == RelationType.MANY_TO_MANY) {

                        JsonArray jsonArray = data.getJsonArray(entry.getValue().getName());

                        if (jsonArray == null) {
                            continue;
                        }

                        if (relationInfo.getRelationTable() != null) {

                            relationTablePrimaryKeySet.add(
                                new RelationTablePrimary(
                                    relationInfo.getRelationTable().getTableName(),
                                    relationInfo.getRelationTable().getLeftColumn(), relationInfo.getRelationTable().getRightColumn()
                                )
                            );
                            relationTablesBuilder.add(relationInfo.getRelationTable().getTableName());

                            for (int i = 0, jsonArraySize = jsonArray.size(); i < jsonArraySize; i++) {
                                JsonObject jsonObject = jsonArray.getJsonObject(i);

                                tableIdSetBuilder.put(childModelInfo.getTable(), jsonObject.getValue(childModelInfo.getPrimaryKey()));

                                relationTableIdSetBuilder.put(relationInfo.getRelationTable().getTableName(),
                                    new RelationColumnValuePairs(
                                        data.getValue(primaryModelInfo.getPrimaryKey()),
                                        jsonObject.getValue(joinModelInfo.getJoinField())
                                    )
                                );

                                mergeRecursive(childModelInfo, jsonObject, tablePrimaryKeySet, tables, tableIdSetBuilder);
                            }
                        } else {

                            for (int i = 0, jsonArraySize = jsonArray.size(); i < jsonArraySize; i++) {
                                JsonObject jsonObject = jsonArray.getJsonObject(i);

                                tableIdSetBuilder.put(childModelInfo.getTable(), jsonObject.getValue(childModelInfo.getPrimaryKey()));

                                mergeRecursive(childModelInfo, jsonObject, tablePrimaryKeySet, tables, tableIdSetBuilder);
                            }
                        }

                    } else {

                        JsonObject jsonObject = data.getJsonObject(entry.getValue().getName());

                        if (jsonObject == null) {
                            continue;
                        }

                        tableIdSetBuilder.put(childModelInfo.getTable(), jsonObject.getValue(childModelInfo.getPrimaryKey()));

                        if (relationInfo.getRelationTable() != null) {

                            relationTablePrimaryKeySet.add(
                                new RelationTablePrimary(
                                    relationInfo.getRelationTable().getTableName(),
                                    relationInfo.getRelationTable().getLeftColumn(),
                                    relationInfo.getRelationTable().getRightColumn()
                                )
                            );
                            relationTablesBuilder.add(relationInfo.getRelationTable().getTableName());

                            relationTableIdSetBuilder.put(relationInfo.getRelationTable().getTableName(),
                                new RelationColumnValuePairs(
                                    data.getValue(primaryModelInfo.getPrimaryKey()),
                                    jsonObject.getValue(joinModelInfo.getJoinField())
                                )
                            );

                            mergeRecursive(childModelInfo, jsonObject, tablePrimaryKeySet, tables, tableIdSetBuilder);

                        } else {

                            mergeRecursive(childModelInfo, jsonObject, tablePrimaryKeySet, tables, tableIdSetBuilder);
                        }

                    }

                    tablePrimaryKeySet.add(new TablePrimaryBuilder()
                        .setTable(childModelInfo.getTable())
                        .setPrimaryKey(childModelInfo.getPrimaryKey())
                        .createTablePrimary());
                    tables.add(childModelInfo.getTable());
                }
            }
        }
    }

}