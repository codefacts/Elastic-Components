package elasta.orm.jpa;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import elasta.core.promise.intfs.Promise;
import elasta.orm.jpa.models.ModelInfo;
import elasta.orm.jpa.models.PropInfo;
import elasta.orm.jpa.models.RelationInfo;
import elasta.orm.json.core.RelationType;
import elasta.orm.json.sql.DbSql;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

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
        String query = finder.queryString(model, data);

        ImmutableSetMultimap<String, Object> multimap1 = finder.tableIdSetBuilder.build();

        List<Object> list1 = finder.tablePrimaryKeySet.build()
            .stream().flatMap(
                tablePrimary -> multimap1.get(tablePrimary.getTable()).stream())
            .collect(Collectors.toList());

        ImmutableSetMultimap<String, RelationColumnValuePairs> multimap2 = finder.relationTableIdSetBuilder.build();

        List<Object> list2 = finder.relationTablePrimaryKeySet.build().stream()
            .flatMap(relationTablePrimary -> multimap2.get(relationTablePrimary.getRelationTable())
                .stream().flatMap(relationColumnValuePairs -> Stream.of(relationColumnValuePairs.getLeftColumnValue(), relationColumnValuePairs.getRightColumnValue())))
            .collect(Collectors.toList());

        return dbSql.query(query, new JsonArray(ImmutableList.builder().addAll(list1).addAll(list2).build()))
            .map(resultSet -> {
                ImmutableSet.Builder<TableIdPair> tableIdPairBuilder = ImmutableSet.builder();
                ImmutableSet.Builder<RelationTableIdPair> relationTableIdPairBuilder = ImmutableSet.builder();
                for (JsonArray jsonArray : resultSet.getResults()) {
                    Iterator arrayIterator = jsonArray.getList().iterator();

                    for (String table : finder.tables.build()) {
                        if (arrayIterator.hasNext()) {
                            Object id = arrayIterator.next();
                            if (id != null) {
                                tableIdPairBuilder.add(new TableIdPairBuilder().setTable(table).setId(id).createTableIdPair());
                            }
                        }
                    }

                    for (String relationTable : finder.relationTables.build()) {
                        relationTableIdPairBuilder.add(
                            new RelationTableIdPair(relationTable, arrayIterator.next(), arrayIterator.next())
                        );
                    }
                }
                return new TableIdPairs(tableIdPairBuilder.build(), relationTableIdPairBuilder.build());
            });
    }

    private class Fineder {
        private final java.lang.CharSequence _OR_ = " or ";
        final ImmutableSet.Builder<TablePrimary> tablePrimaryKeySet = ImmutableSet.builder();
        final ImmutableSet.Builder<RelationTablePrimary> relationTablePrimaryKeySet = ImmutableSet.builder();
        final ImmutableSet.Builder<String> tables = ImmutableSet.builder();
        final ImmutableSet.Builder<String> relationTables = ImmutableSet.builder();
        final ImmutableSetMultimap.Builder<String, Object> tableIdSetBuilder = ImmutableSetMultimap.builder();
        final ImmutableSetMultimap.Builder<String, RelationColumnValuePairs> relationTableIdSetBuilder = ImmutableSetMultimap.builder();

        public String queryString(String model, JsonObject data) {
            ModelInfo modelInfo = modelInfoProvider.get(model);

            tablePrimaryKeySet.add(new TablePrimaryBuilder()
                .setTable(modelInfo.getTable())
                .setPrimaryKey(modelInfo.getPrimaryKey())
                .createTablePrimary());
            tables.add(modelInfo.getTable());
            tableIdSetBuilder.put(modelInfo.getTable(), data.getValue(modelInfo.getPrimaryKey()));

            mergeRecursive(modelInfo, data, tablePrimaryKeySet, tables, tableIdSetBuilder);

            ImmutableSetMultimap<String, Object> multimap = tableIdSetBuilder.build();

            String select = "select " + tablePrimaryKeySet.build().stream().map(tablePrimary -> tablePrimary.getTable() + "." + tablePrimary.getPrimaryKey())
                .collect(Collectors.joining(COMMA));

            String relationSelect = relationTablePrimaryKeySet.build().stream()
                .map(
                    tablePrimary -> tablePrimary.getRelationTable() + "." + tablePrimary.getLeftColumn() + ", " +
                        tablePrimary.getRelationTable() + "." + tablePrimary.getRightColumn()
                )
                .collect(Collectors.joining(COMMA));

            String from = "from " + String.join(COMMA, tables.build());
            String relationFrom = String.join(COMMA, relationTables.build());

            String where = "where " + tablePrimaryKeySet.build().stream().map(
                tablePrimary -> tablePrimary.getTable() + "." + tablePrimary.getPrimaryKey() +
                    " in (" +
                    multimap.get(tablePrimary.getTable()).stream().map(o -> "?").collect(Collectors.joining(COMMA)) +
                    ")"
            ).collect(Collectors.joining(_OR_));

            ImmutableSetMultimap<String, RelationColumnValuePairs> cvpMultimap = relationTableIdSetBuilder.build();

            String relationWhere = relationTablePrimaryKeySet.build().stream().flatMap(
                relationTablePrimary -> {

                    return cvpMultimap.get(relationTablePrimary.getRelationTable()).stream().map(rr -> {

                        return "(" + relationTablePrimary.getRelationTable() + "." + relationTablePrimary.getLeftColumn() +
                            " = ? and " + relationTablePrimary.getRelationTable() + "." + relationTablePrimary.getRightColumn() + " = ?" + ")";
                    });
                }
            ).collect(Collectors.joining(_OR_));

            return select + " " + (relationSelect.isEmpty() ? "" : ", " + relationSelect) + " " +
                from + (relationFrom.isEmpty() ? "" : ", " + relationFrom) + " " +
                where + (relationWhere.isEmpty() ? "" : " or " + relationWhere);
        }

        private void mergeRecursive(ModelInfo primaryModelInfo, JsonObject data,
                                    ImmutableSet.Builder<TablePrimary> tablePrimaryKeySet,
                                    ImmutableSet.Builder<String> tables, ImmutableSetMultimap.Builder<String, Object> tableIdSetBuilder) {

            for (Map.Entry<String, PropInfo> entry : primaryModelInfo.getPropInfoMap().entrySet()) {
                if (entry.getValue().hasRelation()) {
                    RelationInfo relationInfo = entry.getValue().getRelationInfo();

                    ModelInfo modelInfo = modelInfoProvider.get(relationInfo.getJoinModelInfo().getChildModel());

                    if (relationInfo.getRelationType() == RelationType.ONE_TO_MANY
                        || relationInfo.getRelationType() == RelationType.MANY_TO_MANY) {

                        JsonArray jsonArray = data.getJsonArray(entry.getValue().getName());

                        if (jsonArray == null) {
                            continue;
                        }

                        relationTablePrimaryKeySet.add(
                            new RelationTablePrimary(
                                relationInfo.getRelationTable().getTableName(),
                                relationInfo.getRelationTable().getLeftColumn(), relationInfo.getRelationTable().getRightColumn()
                            )
                        );
                        relationTables.add(relationInfo.getRelationTable().getTableName());

                        for (int i = 0, jsonArraySize = jsonArray.size(); i < jsonArraySize; i++) {
                            JsonObject jsonObject = jsonArray.getJsonObject(i);

                            tableIdSetBuilder.put(modelInfo.getTable(), jsonObject.getValue(modelInfo.getPrimaryKey()));

                            relationTableIdSetBuilder.put(relationInfo.getRelationTable().getTableName(),
                                new RelationColumnValuePairs(
                                    data.getValue(primaryModelInfo.getPrimaryKey()),
                                    jsonObject.getValue(relationInfo.getJoinModelInfo().getJoinField())
                                )
                            );

                            mergeRecursive(modelInfo, jsonObject, tablePrimaryKeySet, tables, tableIdSetBuilder);
                        }
                    } else {

                        JsonObject jsonObject = data.getJsonObject(entry.getValue().getName());

                        if (jsonObject == null) {
                            continue;
                        }

                        tableIdSetBuilder.put(modelInfo.getTable(), jsonObject.getValue(modelInfo.getPrimaryKey()));

                        if (relationInfo.getRelationTable() != null) {

                            relationTablePrimaryKeySet.add(
                                new RelationTablePrimary(relationInfo.getRelationTable().getTableName(), relationInfo.getRelationTable().getLeftColumn(), relationInfo.getRelationTable().getRightColumn())
                            );
                            relationTables.add(relationInfo.getRelationTable().getTableName());

                            relationTableIdSetBuilder.put(relationInfo.getRelationTable().getTableName(),
                                new RelationColumnValuePairs(
                                    data.getValue(primaryModelInfo.getPrimaryKey()),
                                    jsonObject.getValue(relationInfo.getJoinModelInfo().getJoinField())
                                )
                            );

                            mergeRecursive(modelInfo, jsonObject, tablePrimaryKeySet, tables, tableIdSetBuilder);

                        } else {

                            mergeRecursive(modelInfo, jsonObject, tablePrimaryKeySet, tables, tableIdSetBuilder);
                        }

                    }

                    tablePrimaryKeySet.add(new TablePrimaryBuilder()
                        .setTable(modelInfo.getTable())
                        .setPrimaryKey(modelInfo.getPrimaryKey())
                        .createTablePrimary());
                    tables.add(modelInfo.getTable());
                }
            }
        }
    }

}