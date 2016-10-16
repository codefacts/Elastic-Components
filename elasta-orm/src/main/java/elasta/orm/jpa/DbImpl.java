package elasta.orm.jpa;

import com.google.common.base.Strings;
import com.google.common.collect.*;
import elasta.commons.Utils;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.MapHandler;
import elasta.core.promise.intfs.Promise;
import elasta.orm.Db;
import elasta.orm.jpa.models.ModelInfo;
import elasta.orm.jpa.models.PropInfo;
import elasta.orm.json.core.FieldInfo;
import elasta.orm.json.core.RelationTable;
import elasta.orm.json.core.RelationType;
import elasta.orm.json.sql.*;
import elasta.orm.json.sql.criteria.SqlAndParams;
import elasta.orm.json.sql.criteria.SqlCriteriaTranslator;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

import static elasta.commons.Utils.not;
import static elasta.commons.Utils.or;

/**
 * Created by Jango on 10/2/2016.
 */
public class DbImpl implements Db {
    private final Jpa jpa;
    private final DbSql dbSql;
    private final ModelInfoProvider modelInfoProvider;
    private final FindExistingIds findExistingIdsImpl;
    private final SqlCriteriaTranslator criteriaUtils;

    public DbImpl(Jpa jpa, DbSql dbSql, ModelInfoProvider modelInfoProvider, FindExistingIds findExistingIdsImpl, SqlCriteriaTranslator criteriaUtils) {
        this.jpa = jpa;
        this.dbSql = dbSql;
        this.modelInfoProvider = modelInfoProvider;
        this.findExistingIdsImpl = findExistingIdsImpl;
        this.criteriaUtils = criteriaUtils;
    }

    @Override
    public <T> Promise<JsonObject> findOne(String model, T id) {

        return jpa.find(jpa.getModelClass(model), id);
    }

    @Override
    public <T> Promise<JsonObject> findOne(String model, T id, List<FieldInfo> selectFields) {

        ModelInfo modelInfo = modelInfoProvider.get(model);

        FieldDetailsInfo fieldDetailsInfo = toFieldDetailsList(selectFields, modelInfo);

        return jpa.queryArray(cb -> {
            Class<T> modelClass = jpa.getModelClass(model);

            CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

            Root<T> root = query.from(modelClass);
            for (int i = 0; i < fieldDetailsInfo.fieldDetailsList.size(); i++) {
                FieldDetails fieldDetails = fieldDetailsInfo.fieldDetailsList.get(i);

                Join<Object, Object> join = null;

                if (Utils.not(fieldDetails.path.isEmpty())) {
                    join = root.join(fieldDetails.path.get(0));
                }

                for (int i1 = 1; i1 < fieldDetails.path.size(); i1++) {
                    String part = fieldDetails.path.get(i1);
                    join = join.join(part);
                }
            }

            query.multiselect(toSelections(fieldDetailsInfo.fieldDetailsList, root));

            query.where(cb.equal(root.get(
                modelInfo.getPrimaryKey()
            ), id));

            return query;
        }).map(toJsonObject(fieldDetailsInfo, modelInfo));
    }

    @Override
    public <T> Promise<List<JsonObject>> findAll(String model, List<T> ids) {

        return jpa.query(cb -> {
            Class<T> modelClass = jpa.getModelClass(model);

            CriteriaQuery<T> query = cb.createQuery(modelClass);

            Root<T> root = query.from(modelClass);

            query.select(root);

            String primaryKey = modelInfoProvider.primaryKey(model);

            query.where(root.get(primaryKey).in(ids));

            return query;
        });
    }

    @Override
    public <T> Promise<List<JsonObject>> findAll(String model, List<T> ids, List<FieldInfo> selectFields) {

        ModelInfo modelInfo = modelInfoProvider.get(model);

        FieldDetailsInfo fieldDetailsInfo = toFieldDetailsList(selectFields, modelInfo);

        return jpa.queryArray(cb -> {

            Class<T> modelClass = jpa.getModelClass(model);

            CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

            Root<T> root = query.from(modelClass);

            query.multiselect(toSelections(fieldDetailsInfo.fieldDetailsList, root));

            query.where(
                root.get(modelInfo.getPrimaryKey()).in(ids)
            );

            return query;
        }).map(toJsonObjectList(fieldDetailsInfo, modelInfo));
    }

    @Override
    public Promise<JsonObject> insertOrUpdate(String model, JsonObject data) {

        return findExistingIdsImpl.findExistingTableIds(model, data)
            .map(pairs -> toUpdateList(model, data, pairs))
            .mapP(dbSql::updateJo)
            .map(voidVoidMutableTpl2 -> data);
    }

    @Override
    public Promise<List<JsonObject>> insertOrUpdateAll(String model, List<JsonObject> jsonObjects) {

        return Promises.when(jsonObjects.stream()
            .map(
                data -> findExistingIdsImpl.findExistingTableIds(model, data)
                    .map(pairs -> toUpdateList(model, data, pairs))
            ).collect(Collectors.toList()))
            .mapP(lists -> dbSql.updateJo(
                lists.stream()
                    .flatMap(tpls -> tpls.stream())
                    .collect(Collectors.toList())
            ))
            .map(aVoid -> jsonObjects);
    }

    @Override
    public <T> Promise<T> delete(String model, T id) {
        ModelInfo modelInfo = modelInfoProvider.get(model);
        return dbSql.update("delete from " + modelInfo.getTable() + " where " + modelInfo.getPrimaryKey() + " = ?", new JsonArray(ImmutableList.of(id))).map(aVoid -> id);
    }

    @Override
    public <T> Promise<List<T>> deleteAll(String model, List<T> ids) {
        ModelInfo modelInfo = modelInfoProvider.get(model);

        return dbSql.update("delete from " + modelInfo.getTable() +
            " where " + modelInfo.getPrimaryKey() + " in (" +
            ids.stream().map(t -> "?").collect(Collectors.joining(", ")) +
            ")", new JsonArray(ids)).map(aVoid -> ids);
    }

    @Override
    public Promise<Long> count(String model) {
        ModelInfo modelInfo = modelInfoProvider.get(model);

        return jpa.queryScalar(cb -> {
            CriteriaQuery<Long> query = cb.createQuery(Long.class);
            Root<Object> root = query.from(jpa.getModelClass(model));
            query.select(cb.count(root.get(modelInfo.getPrimaryKey())));
            return query;
        });
    }

    @Override
    public Promise<Long> count(String model, JsonObject criteria) {
        SqlAndParams sqlAndParams = criteriaUtils.toWhereSql("m.", criteria);
        ModelInfo modelInfo = modelInfoProvider.get(model);
        return jpa.jpqlQueryScalar("select count( m." + modelInfo.getPrimaryKey() + ") from " + model + " m where " + sqlAndParams.getSql(), Long.class, sqlAndParams.getParams());
    }

    @Override
    public Promise<List<JsonObject>> findAll(String model, JsonObject criteria) {
        SqlAndParams sqlAndParams = criteriaUtils.toWhereSql("m.", criteria);
        return jpa.jpqlQuery("select m from " + model + " m where " + sqlAndParams.getSql(), sqlAndParams.getParams());
    }

    @Override
    public Promise<List<JsonObject>> findAll(String model, JsonObject criteria, List<FieldInfo> selectFields) {
        String select = selectFields.stream().flatMap(fieldInfo -> fieldInfo.getFields().stream()
            .map(field -> (fieldInfo.getPath() == null || fieldInfo.getPath().isEmpty() ? "m." : "m." + fieldInfo.getPath() + ".") + field)
        ).collect(Collectors.joining(", "));

        SqlAndParams sqlAndParams = criteriaUtils.toWhereSql("m.", criteria);

        return jpa.jpqlQuery("select " + select + " from " + model + " m where " + sqlAndParams.getSql(), sqlAndParams.getParams());
    }

    private ImmutableList<UpdateTpl> toUpdateList(String model, JsonObject data, TableIdPairs tableIdPairs) {
        ImmutableList.Builder<InsertOrUpdateOperation> operationListBuilder = ImmutableList.builder();
        insertOrUpdateOperationRecursively(modelInfoProvider.get(model), data, tableIdPairs, operationListBuilder);

        ImmutableList.Builder<UpdateTpl> relationsBuilder = ImmutableList.builder();
        ImmutableList.Builder<UpdateTpl> tablesBuilder = ImmutableList.builder();

        operationListBuilder.build().forEach(operation -> {

            if (operation.getRelationTableColumns() != null) {

                if (operation.isInsert()) {
                    relationsBuilder.add(
                        new UpdateTplBuilder()
                            .setUpdateOperationType(UpdateOperationType.INSERT)
                            .setTable(operation.table)
                            .setData(operation.data)
                            .createUpdateTpl()
                    );
                } else {

                    relationsBuilder.add(
                        new UpdateTplBuilder()
                            .setUpdateOperationType(UpdateOperationType.UPDATE)
                            .setTable(operation.table)
                            .setData(operation.data)
                            .setWhere(
                                operation.getTable() + "." + operation.getRelationTableColumns().getLeftColumn() + " = ?" +
                                    operation.getTable() + "." + operation.getRelationTableColumns().getRightColumn() + " = ?"
                            )
                            .setJsonArray(new JsonArray(
                                ImmutableList.of(
                                    operation.data.getValue(operation.getRelationTableColumns().getLeftColumn()),
                                    operation.data.getValue(operation.getRelationTableColumns().getRightColumn())
                                )))
                            .createUpdateTpl()
                    );
                }

            } else {

                if (operation.isInsert()) {

                    tablesBuilder.add(
                        new UpdateTplBuilder()
                            .setUpdateOperationType(UpdateOperationType.INSERT)
                            .setTable(operation.table)
                            .setData(operation.data)
                            .createUpdateTpl()
                    );

                } else {

                    tablesBuilder.add(
                        new UpdateTplBuilder()
                            .setUpdateOperationType(UpdateOperationType.UPDATE)
                            .setTable(operation.table)
                            .setData(operation.data)
                            .setWhere(
                                operation.getTable() + "." + operation.getPrimaryKey() + " = ?"
                            )
                            .setJsonArray(new JsonArray(
                                ImmutableList.of(
                                    operation.data.getValue(operation.getPrimaryKey())
                                )))
                            .createUpdateTpl()
                    );
                }
            }
        });
        return tablesBuilder.addAll(relationsBuilder.build()).build();
    }

    private MapHandler<List<JsonArray>, JsonObject> toJsonObject(FieldDetailsInfo fieldDetailsInfo, ModelInfo modelInfo) {
        return arrays -> {
            LinkedHashMultimap<String, IdAndJo> multimap = toMultimap(arrays, fieldDetailsInfo, modelInfo);

            return new JsonObject(
                toMapRecursive(
                    multimap.get("").stream().findAny().orElseThrow(
                        () -> new OrmException("No Object found for model: " + modelInfo.getName())
                    ).map,
                    multimap, modelInfo
                )
            );
        };
    }

    private MapHandler<List<JsonArray>, List<JsonObject>> toJsonObjectList(FieldDetailsInfo fieldDetailsList, ModelInfo modelInfo) {
        return jsonArrays -> {

            ImmutableList.Builder<JsonObject> listBuilder = ImmutableList.builder();

            LinkedHashMultimap<String, IdAndJo> multimap = toMultimap(jsonArrays, fieldDetailsList, modelInfo);

            Map<Object, Map<String, Object>> roots = multimap.values().stream().filter(idAndJo -> idAndJo.path.isEmpty())
                .peek(idAndJo -> listBuilder.add(new JsonObject(idAndJo.map)))
                .collect(Collectors.toMap(idAndJo -> idAndJo.id, idAndJo -> idAndJo.map));

            for (IdAndJo idAndJo : multimap.values()) {
                if (idAndJo.path.isEmpty()) continue;
                updateData(roots.get(idAndJo.rootId), idAndJo, multimap, modelInfo);
            }
            return listBuilder.build();
        };
    }

    private LinkedHashMultimap<String, IdAndJo> toMultimap(List<JsonArray> arrays, FieldDetailsInfo detailsInfo, ModelInfo modelInfo) {

        LinkedHashMultimap<String, IdAndJo> multimap = LinkedHashMultimap.create();

        for (JsonArray array : arrays) {
            Iterator<Object> iterator = array.iterator();

            for (FieldDetails fieldDetails : detailsInfo.fieldDetailsList) {

                Map<String, Object> map = toMap(fieldDetails, iterator);

                multimap.put(or(fieldDetails.pathStr, ""), new IdAndJo(map.get(modelInfo.getPrimaryKey()), map, fieldDetails.path, array.getValue(detailsInfo.rootIdIndex)));
            }

        }
        return multimap;
    }

    private Map<String, Object> toMapRecursive(final Map<String, Object> rootMap, final LinkedHashMultimap<String, IdAndJo> multimap, final ModelInfo rootModelInfo) {

        for (IdAndJo idAndJo : multimap.values()) {

            if (idAndJo.path.isEmpty()) {
                continue;
            }

            updateData(rootMap, idAndJo, multimap, rootModelInfo);
        }
        return rootMap;
    }

    private void updateData(Map<String, Object> rootMap, IdAndJo idAndJo, LinkedHashMultimap<String, IdAndJo> multimap, ModelInfo rootModelInfo) {
        List<String> path = idAndJo.path;

        ModelInfo modelInfo = rootModelInfo;
        Map<String, Object> map = rootMap;
        for (int i = 0, pathSize_1 = path.size() - 1; i < pathSize_1; i++) {
            String prop = path.get(i);

            PropInfo propInfo = modelInfo.getPropInfoMap().get(prop);

            modelInfo = modelInfoProvider.get(
                propInfo.getRelationInfo().getJoinModelInfo().getChildModel()
            );

            if (propInfo.isSingular()) {

                if (Utils.not(map.containsKey(prop))) {
                    map.put(prop, new LinkedHashMap<>());
                }

                map = (Map<String, Object>) map.get(prop);

            } else {
                throw new OrmException("Model property '" + prop + "' is plural in path '" + idAndJo.path + "'");
            }
        }

        final String lastProp = path.get(path.size() - 1);

        if (modelInfo.getPropInfoMap().get(lastProp).isSingular()) {

            if (Utils.not(map.containsKey(lastProp))) {

                map.put(lastProp, idAndJo.map);

            } else {

                Map<String, Object> mm = (Map<String, Object>) map.get(lastProp);
                mm.putAll(idAndJo.map);
            }

        } else {

            if (Utils.not(map.containsKey(lastProp))) {
                map.put(lastProp, new ArrayList<>());
            }

            List<Map<String, Object>> list = (List<Map<String, Object>>) map.get(lastProp);

            list.add(idAndJo.map);
        }
    }

    private Map<String, Object> toMap(FieldDetails fieldDetails, Iterator<Object> iterator) {
        Map<String, Object> map = new LinkedHashMap<>();

        for (String field : fieldDetails.fields) {
            map.put(field, iterator.next());
        }

        return map;
    }

    private FieldDetailsInfo toFieldDetailsList(List<FieldInfo> selectFields, ModelInfo modelInfo) {
        ImmutableList.Builder<FieldDetails> fieldListBuilder = ImmutableList.builder();

        int rootIdIndex = 0;
        boolean present = selectFields.stream().filter(fieldInfo -> fieldInfo.getPath() == null || fieldInfo.getPath().isEmpty()).findAny().isPresent();

        if (not(present)) {
            fieldListBuilder.add(
                new FieldDetails("", Collections.emptyList(), Collections.singletonList(modelInfo.getPrimaryKey()))
            );
        }

        int len = 0;
        for (FieldInfo fieldInfo : selectFields) {
            List<String> fields = fieldInfo.getFields();
            fields = fields.contains(modelInfo.getPrimaryKey()) ? fields
                : ImmutableList.<String>builder().add(modelInfo.getPrimaryKey()).addAll(fields).build();

            fieldListBuilder.add(
                new FieldDetails(fieldInfo.getPath(), toParts(fieldInfo.getPath()), fields)
            );

            if (fieldInfo.getPath() == null || fieldInfo.getPath().isEmpty()) {
                rootIdIndex = len + fields.indexOf(modelInfo.getPrimaryKey());
            }

            len += fields.size();
        }

        return new FieldDetailsInfo(rootIdIndex, fieldListBuilder.build());
    }

    private List<String> toParts(String path) {
        if (Strings.isNullOrEmpty(path)) {
            return Collections.emptyList();
        }
        return Arrays.asList(path.split("\\."));
    }

    private <T> List<Selection<?>> toSelections(List<FieldDetails> selectFields, Root<T> root) {
        ImmutableList.Builder<Selection<?>> listBuilder = ImmutableList.builder();

        selectFields.forEach(fieldInfo -> {
            Path<T> path = toPath(fieldInfo.path, root);

            fieldInfo.fields.forEach(field -> listBuilder.add(path.get(field)));
        });

        return listBuilder.build();
    }

    private <T> Path<T> toPath(List<String> path, Root<T> root) {

        if (path.isEmpty()) {
            return root;
        }

        Path<T> pp = root;
        for (String part : path) {
            pp = pp.get(part);
        }

        return pp;
    }

    private static class FieldDetails {
        final String pathStr;
        final List<String> path;
        final List<String> fields;

        private FieldDetails(String pathStr, List<String> path, List<String> fields) {
            this.pathStr = pathStr;
            this.path = path;
            this.fields = fields;
        }
    }

    private void insertOrUpdateOperationRecursively(ModelInfo modelInfo, JsonObject data, TableIdPairs tableIdPairs,
                                                    ImmutableList.Builder<InsertOrUpdateOperation> operationListBuilder) {
        ImmutableMap.Builder<String, Object> mapBuilder = ImmutableMap.builder();
        for (Map.Entry<String, PropInfo> entry : modelInfo.getPropInfoMap().entrySet()) {
            if (entry.getValue().hasRelation()) {

                if (entry.getValue().getRelationInfo().getRelationType() == RelationType.ONE_TO_MANY) {

                    mergeCollection(entry, modelInfo, data, tableIdPairs, operationListBuilder);

                } else if (entry.getValue().getRelationInfo().getRelationType() == RelationType.MANY_TO_MANY) {

                    mergeCollection(entry, modelInfo, data, tableIdPairs, operationListBuilder);

                } else if (entry.getValue().hasRelationTable()) {

                    JsonObject jsonObject = data.getJsonObject(entry.getValue().getName());

                    if (jsonObject == null) {
                        continue;
                    }

                    operationListBuilder.add(relation(entry, modelInfo, data, jsonObject, tableIdPairs));

                    insertOrUpdateOperationRecursively(
                        modelInfoProvider.get(entry.getValue().getRelationInfo().getJoinModelInfo().getChildModel()),
                        jsonObject, tableIdPairs, operationListBuilder
                    );

                } else {

                    JsonObject jsonObject = data.getJsonObject(entry.getValue().getName());

                    if (jsonObject == null) {
                        continue;
                    }

                    insertOrUpdateOperationRecursively(
                        modelInfoProvider.get(entry.getValue().getRelationInfo().getJoinModelInfo().getChildModel()),
                        jsonObject, tableIdPairs, operationListBuilder
                    );
                }

            } else {

                Object value = data.getValue(entry.getValue().getName());

                if (value != null) {

                    mapBuilder.put(entry.getValue().getColumn(), value);
                }
            }
        }

        operationListBuilder.add(
            new InsertOrUpdateOperationBuilder()
                .setInsert(
                    Utils.not(tableIdPairs.getTableIdPairs().contains(
                        new TableIdPairBuilder()
                            .setId(data.getValue(modelInfo.getPrimaryKey()))
                            .setTable(modelInfo.getTable())
                            .createTableIdPair()
                    ))
                )
                .setTable(modelInfo.getTable())
                .setPrimaryKey(modelInfo.getPrimaryKey())
                .setData(new JsonObject(mapBuilder.build()))
                .createInsertOrUpdateOperation()
        );

    }

    private void mergeCollection(Map.Entry<String, PropInfo> entry, ModelInfo modelInfo, JsonObject data, TableIdPairs tableIdPairs, ImmutableList.Builder<InsertOrUpdateOperation> operationListBuilder) {
        JsonArray jsonArray = data.getJsonArray(entry.getValue().getName());

        if (jsonArray == null) {
            return;
        }

        for (int i = 0; i < jsonArray.size(); i++) {

            JsonObject jsonObject = jsonArray.getJsonObject(i);

            operationListBuilder.add(relation(entry, modelInfo, data, jsonObject, tableIdPairs));

            insertOrUpdateOperationRecursively(
                modelInfoProvider.get(entry.getValue().getRelationInfo().getJoinModelInfo().getChildModel()),
                jsonObject, tableIdPairs, operationListBuilder
            );
        }
    }

    private InsertOrUpdateOperation relation(Map.Entry<String, PropInfo> entry, ModelInfo modelInfo, JsonObject data, JsonObject jsonObject, TableIdPairs tableIdPairs) {

        RelationTable relationTable = entry.getValue().getRelationInfo().getRelationTable();

        Object modelId = data.getValue(modelInfo.getPrimaryKey());
        Object childModelId = jsonObject.getValue(entry.getValue().getRelationInfo().getJoinModelInfo().getJoinField());

        return new InsertOrUpdateOperationBuilder()
            .setInsert(
                tableIdPairs.getRelationTableIdPairs().contains(
                    new RelationTableIdPair(
                        relationTable.getTableName(),
                        modelId,
                        childModelId
                    )
                )
            ).setTable(relationTable.getTableName())
            .setRelationTableColumns(
                new RelationTableColumns(relationTable.getLeftColumn(), relationTable.getRightColumn())
            ).setData(
                new JsonObject(
                    ImmutableMap.of(
                        relationTable.getLeftColumn(), modelId,
                        relationTable.getRightColumn(), childModelId
                    )
                )
            ).createInsertOrUpdateOperation();
    }

    static class InsertOrUpdateOperation {
        final boolean insert;
        final String table;
        final String primaryKey;
        final RelationTableColumns relationTableColumns;
        final JsonObject data;

        public InsertOrUpdateOperation(boolean insert, String table, String primaryKey, RelationTableColumns relationTableColumns, JsonObject data) {
            this.insert = insert;
            this.table = table;
            this.primaryKey = primaryKey;
            this.relationTableColumns = relationTableColumns;
            this.data = data;
        }

        public boolean isInsert() {
            return insert;
        }

        public String getTable() {
            return table;
        }

        public String getPrimaryKey() {
            return primaryKey;
        }

        public RelationTableColumns getRelationTableColumns() {
            return relationTableColumns;
        }

        public JsonObject getData() {
            return data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            InsertOrUpdateOperation that = (InsertOrUpdateOperation) o;

            if (insert != that.insert) return false;
            if (table != null ? !table.equals(that.table) : that.table != null) return false;
            if (primaryKey != null ? !primaryKey.equals(that.primaryKey) : that.primaryKey != null) return false;
            if (relationTableColumns != null ? !relationTableColumns.equals(that.relationTableColumns) : that.relationTableColumns != null)
                return false;
            return data != null ? data.equals(that.data) : that.data == null;

        }

        @Override
        public int hashCode() {
            int result = (insert ? 1 : 0);
            result = 31 * result + (table != null ? table.hashCode() : 0);
            result = 31 * result + (primaryKey != null ? primaryKey.hashCode() : 0);
            result = 31 * result + (relationTableColumns != null ? relationTableColumns.hashCode() : 0);
            result = 31 * result + (data != null ? data.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "InsertOrUpdateOperation{" +
                "insert=" + insert +
                ", table='" + table + '\'' +
                ", primaryKey='" + primaryKey + '\'' +
                ", relationTableColumns=" + relationTableColumns +
                ", data=" + data +
                '}';
        }
    }

    private static class IdAndJo {
        final Object id;
        final Map<String, Object> map;
        final List<String> path;
        final Object rootId;

        private IdAndJo(Object id, Map<String, Object> map, List<String> path, Object rootId) {
            this.id = id;
            this.map = map;
            this.path = path;
            this.rootId = rootId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IdAndJo idAndJo = (IdAndJo) o;

            return id != null ? id.equals(idAndJo.id) : idAndJo.id == null;

        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "IdAndJo{" +
                "id=" + id +
                ", map=" + map +
                '}';
        }
    }

    private static class FieldDetailsInfo {
        final int rootIdIndex;
        final List<FieldDetails> fieldDetailsList;

        private FieldDetailsInfo(int rootIdIndex, List<FieldDetails> fieldDetailsList) {
            this.rootIdIndex = rootIdIndex;
            this.fieldDetailsList = fieldDetailsList;
        }
    }
}
