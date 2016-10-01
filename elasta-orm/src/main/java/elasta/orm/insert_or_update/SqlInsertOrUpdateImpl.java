package elasta.orm.insert_or_update;

import com.google.common.collect.ImmutableList;
import elasta.core.promise.intfs.Promise;
import elasta.orm.sql.DbSql;
import elasta.orm.sql.DbSqlImpl;
import elasta.orm.sql.SqlUtils;
import elasta.orm.sql.TestCases;
import elasta.orm.sql.core.ColumnSpec;
import elasta.orm.sql.core.TableSpec;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;

/**
 * Created by Jango on 9/24/2016.
 */
public class SqlInsertOrUpdateImpl implements SqlInsertOrUpdate {
    private static final String _OR_ = " or ";
    private final Map<String, TableSpec> tableSpecMap;
    private final Map<String, Map<String, ColumnSpec>> columnSpecMapByNameMap;
    private final DbSql dbSql;
    private static final String COMMA = ", ";

    public SqlInsertOrUpdateImpl(Map<String, TableSpec> tableSpecMap, Map<String, Map<String, ColumnSpec>> columnSpecMapByNameMap, DbSql dbSql) {
        this.tableSpecMap = tableSpecMap;
        this.columnSpecMapByNameMap = columnSpecMapByNameMap;
        this.dbSql = dbSql;
    }

    @Override
    public Promise<List<UpdateSpec>> insertOrUpdate(String rootTable, JsonObject jsonObject) {
//        ImmutableList.Builder<TableInfo> infoBuilder = ImmutableList.builder();
//        ImmutableList.Builder<ObjectData> listBuilder = ImmutableList.builder();
//
//        traverseRecursive(jsonObject.getMap(), listBuilder, infoBuilder, tableSpecMap.get(rootTable), columnSpecMapByNameMap.get(rootTable));
//
//        ImmutableList<TableInfo> infos = infoBuilder.build();
//        ImmutableList<ObjectData> list = listBuilder.build();
//
//        StringBuilder stringBuilder = new StringBuilder("select ");
//        StringBuilder joinBuilder = new StringBuilder("from ");
//        StringBuilder whereBuilder = new StringBuilder("where ");
//        JsonArray jsonArray = new JsonArray();
//
//        for (int i = 0, infosSize = infos.size(); i < infosSize; i++) {
//            TableInfo info = infos.get(i);
//            TableSpec tableSpec = tableSpecMap.get(info.tableName);
////            stringBuilder.append(tableSpec.getTableName()).append(".").append(tableSpec.getPrimaryKey()).append(COMMA);
//            joinBuilder.append(info.tableName).append(COMMA);
////            whereBuilder.append("(").append(info.tableName).append(".").append(tableSpec.getPrimaryKey()).append(" = ?").append(")").append(_OR_);
//            jsonArray.add(info.id);
//        }
//
//        if (not(infos.isEmpty())) {
//            stringBuilder.delete(stringBuilder.length() - COMMA.length(), stringBuilder.length());
//            joinBuilder.delete(joinBuilder.length() - COMMA.length(), joinBuilder.length());
//            whereBuilder.delete(whereBuilder.length() - _OR_.length(), whereBuilder.length());
//        }
//
//        dbSql.query(stringBuilder.append(" ").append(joinBuilder).append(" ").append(whereBuilder).toString(), jsonArray)
//            .then(resultSet -> {
//
//                Map<String, Boolean> statusMap = new HashMap<>();
//
//                resultSet.getResults().forEach(array -> {
//                    for (int i = 0; i < infos.size(); i++) {
//                        TableInfo info = infos.get(i);
//                        TableSpec tableSpec = tableSpecMap.get(info.tableName);
//                        String key = info.tableName + "." + tableSpec.getPrimaryKey();
//                        if (not(statusMap.get(key))) {
//                            statusMap.put(key, array.getValue(i) != null);
//                        }
//                    }
//                });
//
//                ImmutableList.Builder<Object> updateSpecBuilder = ImmutableList.builder();
//
//                for (int i = 0; i < list.size(); i++) {
//                    ObjectData objectData = list.get(i);
//                    String key = objectData.getTable() + "." + objectData.getPrimaryKey();
//                    if (statusMap.get(key)) {
//
//                        updateSpecBuilder.add(
//                            updateSpec(objectData)
//                        );
//                    } else {
//                        insertSpec(objectData);
//                    }
//                }
//            })
//        ;
//
        return null;
    }

    private UpdateSpec insertSpec(ObjectData objectData) {

        ImmutableList.Builder<Object> paramsBuilder = ImmutableList.builder();
        for (Map.Entry<String, Object> entry : objectData.getData().entrySet()) {

        }
        return null;
    }

    private UpdateSpec updateSpec(ObjectData objectData) {
        return null;
    }

//    private void traverseRecursive(
//        Map<String, Object> map,
//        ImmutableList.Builder<ObjectData> listBuilder, ImmutableList.Builder<TableInfo> infoBuilder,
//        TableSpec tableSpec, Map<String, ColumnSpec> columnSpecMap) {
//
//        Object id = map.get(tableSpec.getPrimaryKey());
//        infoBuilder.add(new TableInfo(tableSpec.getTableName(), id));
//        listBuilder.add(new ObjectData(tableSpec.getTableName(), tableSpec.getPrimaryKey(), map));
//
//        for (Map.Entry<String, Object> entry : map.entrySet()) {
//
//            Object value = entry.getValue();
//            traverse(entry.getKey(), value, listBuilder, infoBuilder, columnSpecMap);
//        }
//    }
//
//    private void traverse(
//        String key, Object value,
//        ImmutableList.Builder<ObjectData> listBuilder, ImmutableList.Builder<TableInfo> infoBuilder, Map<String, ColumnSpec> columnSpecMap) {
//
//        ColumnSpec columnSpec = columnSpecMap.get(key);
//
//        if (columnSpec.getJoinSpec() == null) {
//            return;
//        }
//
//        TableSpec tblSpec = tableSpecMap.get(columnSpec.getJoinSpec().getJoinTable());
//        Map<String, ColumnSpec> specMap = columnSpecMapByNameMap.get(columnSpec.getJoinSpec().getJoinTable());
//
//        if (value instanceof JsonObject) {
//
//            traverseRecursive(((JsonObject) value).getMap(), listBuilder, infoBuilder, tblSpec, specMap);
//
//        } else if (value instanceof Map) {
//
//            traverseRecursive((Map<String, Object>) value, listBuilder, infoBuilder, tblSpec, specMap);
//
//        } else if (value instanceof List) {
//
//            ((List) value).forEach(obj -> {
//
//                traverse(key, value, listBuilder, infoBuilder, columnSpecMap);
//            });
//
//        } else if (value instanceof JsonArray) {
//
//            ((JsonArray) value).forEach(o -> {
//                traverse(key, value, listBuilder, infoBuilder, columnSpecMap);
//            });
//        }
//    }

    private static class TableInfo {
        private final String tableName;
        private final Object id;

        private TableInfo(String tableName, Object id) {
            this.tableName = tableName;
            this.id = id;
        }
    }

    public static void main(String[] args) {
        List<TableSpec> list = TestCases.createList();
        list = SqlUtils.makeDefaults(list);
        Map<String, TableSpec> stringTableSpecMap = SqlUtils.toTableSpecByTableMap(list);
        SqlInsertOrUpdateImpl update = new SqlInsertOrUpdateImpl(stringTableSpecMap, null, new DbSqlImpl());

        update.insertOrUpdate(
            "contact", new JsonObject()
                .put("id", 45)
                .put("name", "sohan")
                .put("email", "email")
                .put("br_id",
                    new JsonObject()
                        .put("id", 34)
                        .put(
                            "supervisor_id", new JsonObject()
                                .put("id", 34)
                                .put("ac_id",
                                    new JsonObject().put("id", 909)
                                )
                        )
                )
                .put("house_id",
                    new JsonObject()
                        .put("id", 8989)
                        .put(
                            "area_id",
                            new JsonObject()
                                .put("id", 89)
                                .put(
                                    "region_id",
                                    new JsonObject()
                                        .put("id", 90)
                                )
                        )
                )
        );
    }
}
