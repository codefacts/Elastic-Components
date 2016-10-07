package elasta.orm.jpa;

import com.google.common.collect.ImmutableSetMultimap;
import elasta.core.promise.intfs.Promise;
import elasta.orm.jpa.models.ModelInfo;
import elasta.orm.jpa.models.PropInfo;
import elasta.orm.jpa.models.RelationInfo;
import elasta.orm.json.core.RelationType;
import elasta.orm.json.sql.DbSql;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Shahadat on 10/6/2016.
 */
public class IU {
    private final String COMMA = ", ";
    private final ModelInfoProvider modelInfoProvider;
    private final DbSql dbSql;

    public IU(ModelInfoProvider modelInfoProvider, DbSql dbSql) {
        this.modelInfoProvider = modelInfoProvider;
        this.dbSql = dbSql;
    }

    public Promise<Map<TableIdPair, Boolean>> updateInfos(String model, JsonObject data) {

        ModelInfo modelInfo = modelInfoProvider.get(model);
        Set<TablePrimary> tablePrimaryKeySet = new LinkedHashSet<>();
        Set<String> tables = new LinkedHashSet<>();
        ImmutableSetMultimap.Builder<String, Object> tableIdSetBuilder = ImmutableSetMultimap.builder();

        tablePrimaryKeySet.add(new TablePrimaryBuilder()
            .setTable(modelInfo.getTable())
            .setPrimaryKey(modelInfo.getPrimaryKey())
            .createTablePrimary());
        tables.add(modelInfo.getTable());
        tableIdSetBuilder.put(modelInfo.getTable(), data.getValue(modelInfo.getPrimaryKey()));
        mergeRecursive(modelInfo, data, tablePrimaryKeySet, tables, tableIdSetBuilder);

        ImmutableSetMultimap<String, Object> multimap = tableIdSetBuilder.build();

        String select = "select " + tablePrimaryKeySet.stream().map(tablePrimary -> tablePrimary.getTable() + "." + tablePrimary.getPrimaryKey()).collect(Collectors.joining(COMMA));
        String from = "from " + String.join(COMMA, tables);
        String where = "where " + tablePrimaryKeySet.stream().map(
            tablePrimary -> tablePrimary.getTable() + "." + tablePrimary.getPrimaryKey() +
                " in (" +
                multimap.get(tablePrimary.getTable()).stream().map(o -> "?").collect(Collectors.joining(COMMA)) +
                ")"
        ).collect(Collectors.joining(COMMA));

        dbSql.query(select + " " + from + " " + where, new JsonArray(
            tablePrimaryKeySet.stream().flatMap(tablePrimary -> Stream.of(multimap.get(tablePrimary.getTable()))).collect(Collectors.toList())
        )).then(resultSet -> {

        });



        return null;
    }

    private void mergeRecursive(ModelInfo modelInfo, JsonObject data,
                                Set<TablePrimary> tablePrimaryKeySet, Set<String> tables, ImmutableSetMultimap.Builder<String, Object> tableIdSetBuilder) {
        for (Map.Entry<String, PropInfo> entry : modelInfo.getPropInfoMap().entrySet()) {
            if (entry.getValue().hasRelation()) {
                RelationInfo relationInfo = entry.getValue().getRelationInfo();

                modelInfo = modelInfoProvider.get(relationInfo.getJoinTableInfo().getJoinTable());

                tablePrimaryKeySet.add(new TablePrimaryBuilder()
                    .setTable(modelInfo.getTable())
                    .setPrimaryKey(modelInfo.getPrimaryKey())
                    .createTablePrimary());
                tables.add(modelInfo.getTable());

                if (relationInfo.getRelationType() == RelationType.ONE_TO_MANY
                    || relationInfo.getRelationType() == RelationType.MANY_TO_MANY) {

                    List<JsonObject> joList = data.getJsonArray(entry.getValue().getName()).getList();

                    for (JsonObject jsonObject : joList) {
                        tableIdSetBuilder.put(modelInfo.getTable(), jsonObject.getValue(modelInfo.getPrimaryKey()));
                        mergeRecursive(modelInfo, jsonObject, tablePrimaryKeySet, tables, tableIdSetBuilder);
                    }
                } else {
                    JsonObject jsonObject = data.getJsonObject(entry.getValue().getName());
                    tableIdSetBuilder.put(modelInfo.getTable(), jsonObject.getValue(modelInfo.getPrimaryKey()));
                    mergeRecursive(modelInfo, jsonObject, tablePrimaryKeySet, tables, tableIdSetBuilder);
                }
            }
        }
    }
}