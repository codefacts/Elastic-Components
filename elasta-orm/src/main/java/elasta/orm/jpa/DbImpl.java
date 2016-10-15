package elasta.orm.jpa;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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

        ModelInfo modelInfo = modelInfoProvider.get(model);
        return jpa.find(jpa.getModelClass(model), id);
    }

    @Override
    public <T> Promise<JsonObject> findOne(String model, T id, List<FieldInfo> selectFields) {

        ModelInfo modelInfo = modelInfoProvider.get(model);

        List<FieldDetails> fieldDetailsList = toFieldDetailsList(selectFields, modelInfo);

        return jpa.queryArray(cb -> {
            Class<T> modelClass = jpa.getModelClass(model);

            CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

            Root<T> root = query.from(modelClass);
            for (int i = 0; i < fieldDetailsList.size(); i++) {
                FieldDetails fieldDetails = fieldDetailsList.get(i);

                Join<Object, Object> join = null;

                if (Utils.not(fieldDetails.path.isEmpty())) {
                    join = root.join(fieldDetails.path.get(0));
                }

                for (int i1 = 1; i1 < fieldDetails.path.size(); i1++) {
                    String part = fieldDetails.path.get(i1);
                    join = join.join(part);
                }
            }

            query.multiselect(toSelections(fieldDetailsList, root));

            query.where(cb.equal(root.get(
                modelInfo.getPrimaryKey()
            ), id));

            return query;
        }).map(toJsonObject(fieldDetailsList, modelInfo));
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

        List<FieldDetails> fieldDetailsList = toFieldDetailsList(selectFields, modelInfo);

        return jpa.queryArray(cb -> {

            Class<T> modelClass = jpa.getModelClass(model);

            CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

            Root<T> root = query.from(modelClass);

            query.multiselect(toSelections(fieldDetailsList, root));

            Predicate[] predicates = new Predicate[ids.size()];

            String primaryKey = modelInfo.getPrimaryKey();

            for (int i = 0; i < predicates.length; i++) {
                predicates[i] = cb.equal(root.get(primaryKey), ids.get(i));
            }

            query.where(cb.equal(root.get(primaryKey), cb.or(predicates)));

            return query;
        }).map(
            jsonArrayList -> jsonArrayList
                .stream()
                .map(ja -> new JsonObject())
                .collect(Collectors.toList())
        );
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
        ImmutableList.Builder<UpdateTpl> updateListBuilder = ImmutableList.builder();
        operationListBuilder.build().forEach(operation -> {
            if (operation.isInsert()) {

                updateListBuilder.add(
                    new UpdateTplBuilder()
                        .setUpdateOperationType(UpdateOperationType.INSERT)
                        .setTable(operation.table)
                        .setData(operation.data)
                        .createUpdateTpl()
                );

            } else {

                if (operation.getRelationTableColumns() != null) {

                    updateListBuilder.add(
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
                } else {
                    updateListBuilder.add(
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
        return updateListBuilder.build();
    }

    private MapHandler<List<JsonArray>, JsonObject> toJsonObject(List<FieldDetails> fieldDetailsList, ModelInfo modelInfo) {
        return arrays -> convertToJo(arrays, fieldDetailsList, modelInfo);
    }

    private JsonObject convertToJo(List<JsonArray> arrays, List<FieldDetails> fieldDetailsList, ModelInfo modelInfo) {

        HashMap<String, Object> hashMap = new HashMap<>();


        return new JsonObject(hashMap);
    }

    private List<FieldDetails> toFieldDetailsList(List<FieldInfo> selectFields, ModelInfo modelInfo) {
        ImmutableList.Builder<FieldDetails> fieldListBuilder = ImmutableList.builder();

        selectFields.forEach(
            fieldInfo -> {

                List<String> fields = fieldInfo.getFields();
                fields = fields.contains(modelInfo.getPrimaryKey()) ? fields
                    : ImmutableList.<String>builder().add(modelInfo.getPrimaryKey()).addAll(fields).build();

                fieldListBuilder.add(
                    new FieldDetails(fieldInfo.getPath(), toParts(fieldInfo.getPath()), fields)
                );
            });

        return fieldListBuilder.build();
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

    private void insertOrUpdateOperationRecursively(ModelInfo modelInfo, JsonObject data, TableIdPairs tableIdPairs, ImmutableList.Builder<InsertOrUpdateOperation> operationListBuilder) {
        ImmutableMap.Builder<String, Object> mapBuilder = ImmutableMap.builder();
        for (Map.Entry<String, PropInfo> entry : modelInfo.getPropInfoMap().entrySet()) {
            if (entry.getValue().hasRelation()) {

                if (entry.getValue().getRelationInfo().getRelationType() == RelationType.ONE_TO_MANY) {

                    mergeCollection(entry, modelInfo, data, tableIdPairs, operationListBuilder);

                } else if (entry.getValue().getRelationInfo().getRelationType() == RelationType.MANY_TO_MANY) {

                    mergeCollection(entry, modelInfo, data, tableIdPairs, operationListBuilder);

                } else if (entry.getValue().hasRelationTable()) {

                    JsonObject jsonObject = data.getJsonObject(entry.getValue().getName());

                    operationListBuilder.add(relation(entry, modelInfo, data, jsonObject, tableIdPairs));

                    insertOrUpdateOperationRecursively(
                        modelInfoProvider.get(entry.getValue().getRelationInfo().getJoinModelInfo().getChildModel()),
                        jsonObject, tableIdPairs, operationListBuilder
                    );

                } else {

                    insertOrUpdateOperationRecursively(
                        modelInfoProvider.get(entry.getValue().getRelationInfo().getJoinModelInfo().getChildModel()),
                        data.getJsonObject(entry.getValue().getName()), tableIdPairs, operationListBuilder
                    );
                }

            } else {
                mapBuilder.put(entry.getValue().getColumn(), data.getValue(entry.getValue().getName()));
            }
        }
    }

    private void mergeCollection(Map.Entry<String, PropInfo> entry, ModelInfo modelInfo, JsonObject data, TableIdPairs tableIdPairs, ImmutableList.Builder<InsertOrUpdateOperation> operationListBuilder) {
        JsonArray jsonArray = data.getJsonArray(entry.getValue().getName());

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
}
