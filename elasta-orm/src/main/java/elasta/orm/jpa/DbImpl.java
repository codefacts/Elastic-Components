package elasta.orm.jpa;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.MapHandler;
import elasta.core.promise.intfs.Promise;
import elasta.core.touple.immutable.Tpls;
import elasta.orm.Db;
import elasta.orm.jpa.models.ModelInfo;
import elasta.orm.jpa.models.PropInfo;
import elasta.orm.json.core.FieldInfo;
import elasta.orm.json.core.RelationTable;
import elasta.orm.json.core.RelationType;
import elasta.orm.json.sql.DbSql;
import elasta.orm.json.sql.InsertTpl;
import elasta.orm.json.sql.UpdateTpl;
import elasta.orm.json.sql.UpdateTplBuilder;
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
    private final IU iu;

    public DbImpl(Jpa jpa, DbSql dbSql, ModelInfoProvider modelInfoProvider, IU iu) {
        this.jpa = jpa;
        this.dbSql = dbSql;
        this.modelInfoProvider = modelInfoProvider;
        this.iu = iu;
    }

    @Override
    public <T> Promise<JsonObject> findOne(String model, T id) {

        ModelInfo modelInfo = modelInfoProvider.get(model);
        return jpa.find(jpa.getModelClass(model), id);
    }

    @Override
    public <T> Promise<JsonObject> findOne(String model, T id, List<FieldInfo> selectFields) {

        List<FieldDetails> fieldDetailsList = toFieldDetailsList(selectFields);

        Promise<JsonArray> promise = jpa.querySingleArray(cb -> {
            Class<T> modelClass = jpa.getModelClass(model);

            CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

            Root<T> root = query.from(modelClass);

            query.multiselect(toSelections(fieldDetailsList, root));

            query.where(cb.equal(root.get(modelInfoProvider.primaryKey(model)), id));

            return query;
        });

        return promise.map(toJsonObject(fieldDetailsList));
    }

    @Override
    public <T> Promise<List<JsonObject>> findAll(String model, List<T> ids) {

        return jpa.query(cb -> {
            Class<T> modelClass = jpa.getModelClass(model);

            CriteriaQuery<T> query = cb.createQuery(modelClass);

            Root<T> root = query.from(modelClass);

            query.select(root);

            Predicate[] predicates = new Predicate[ids.size()];

            String primaryKey = modelInfoProvider.primaryKey(model);

            for (int i = 0; i < predicates.length; i++) {
                predicates[i] = cb.equal(root.get(primaryKey), ids.get(i));
            }

            query.where(cb.equal(root.get(primaryKey), cb.or(predicates)));

            return query;
        });
    }

    @Override
    public <T> Promise<List<JsonObject>> findAll(String model, List<T> ids, List<FieldInfo> selectFields) {

        List<FieldDetails> fieldDetailsList = toFieldDetailsList(selectFields);

        return jpa.queryArray(cb -> {

            Class<T> modelClass = jpa.getModelClass(model);

            CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

            Root<T> root = query.from(modelClass);

            query.multiselect(toSelections(fieldDetailsList, root));

            Predicate[] predicates = new Predicate[ids.size()];

            String primaryKey = modelInfoProvider.primaryKey(model);

            for (int i = 0; i < predicates.length; i++) {
                predicates[i] = cb.equal(root.get(primaryKey), ids.get(i));
            }

            query.where(cb.equal(root.get(primaryKey), cb.or(predicates)));

            return query;
        }).map(
            jsonArrayList -> jsonArrayList
                .stream()
                .map(ja -> convertToJa(ja, fieldDetailsList))
                .collect(Collectors.toList())
        );
    }

    @Override
    public Promise<JsonObject> insertOrUpdate(String model, JsonObject data) {

        return iu.findExistingTableIds(model, data)
            .map(pairs -> {
                ImmutableList.Builder<InsertOrUpdateOperation> operationListBuilder = ImmutableList.builder();
                insertOrUpdateOperationRecursively(modelInfoProvider.get(model), data, pairs, operationListBuilder);
                ImmutableList.Builder<InsertTpl> insertListBuilder = ImmutableList.builder();
                ImmutableList.Builder<UpdateTpl> updateListBuilder = ImmutableList.builder();
                operationListBuilder.build().forEach(operation -> {
                    if (operation.isInsert()) {

                        insertListBuilder.add(
                            new InsertTpl(operation.getTable(), operation.data)
                        );

                    } else {

                        if (operation.getRelationTableColumns() != null) {

                            updateListBuilder.add(
                                new UpdateTplBuilder()
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
                return Tpls.of(insertListBuilder.build(), updateListBuilder.build());
            })
            .mapP(tpl2 -> Promises.when(dbSql.insertJo(tpl2.t1), dbSql.updateJo(tpl2.t2)))
            .map(voidVoidMutableTpl2 -> data);
    }

    private void insertOrUpdateOperationRecursively(ModelInfo modelInfo, JsonObject data, Pairs pairs, ImmutableList.Builder<InsertOrUpdateOperation> operationListBuilder) {
        ImmutableMap.Builder<String, Object> mapBuilder = ImmutableMap.builder();
        for (Map.Entry<String, PropInfo> entry : modelInfo.getPropInfoMap().entrySet()) {
            if (entry.getValue().hasRelation()) {

                if (entry.getValue().getRelationInfo().getRelationType() == RelationType.ONE_TO_MANY) {

                    mergeCollection(entry, modelInfo, data, pairs, operationListBuilder);

                } else if (entry.getValue().getRelationInfo().getRelationType() == RelationType.MANY_TO_MANY) {

                    mergeCollection(entry, modelInfo, data, pairs, operationListBuilder);

                } else if (entry.getValue().hasRelationTable()) {

                    JsonObject jsonObject = data.getJsonObject(entry.getValue().getName());

                    operationListBuilder.add(relation(entry, modelInfo, data, jsonObject, pairs));

                    insertOrUpdateOperationRecursively(
                        modelInfoProvider.get(entry.getValue().getRelationInfo().getJoinModelInfo().getJoinModel()),
                        jsonObject, pairs, operationListBuilder
                    );

                } else {

                    insertOrUpdateOperationRecursively(
                        modelInfoProvider.get(entry.getValue().getRelationInfo().getJoinModelInfo().getJoinModel()),
                        data.getJsonObject(entry.getValue().getName()), pairs, operationListBuilder
                    );
                }

            } else {
                mapBuilder.put(entry.getValue().getColumn(), data.getValue(entry.getValue().getName()));
            }
        }
    }

    private void mergeCollection(Map.Entry<String, PropInfo> entry, ModelInfo modelInfo, JsonObject data, Pairs pairs, ImmutableList.Builder<InsertOrUpdateOperation> operationListBuilder) {
        JsonArray jsonArray = data.getJsonArray(entry.getValue().getName());

        for (int i = 0; i < jsonArray.size(); i++) {

            JsonObject jsonObject = jsonArray.getJsonObject(i);

            operationListBuilder.add(relation(entry, modelInfo, data, jsonObject, pairs));

            insertOrUpdateOperationRecursively(
                modelInfoProvider.get(entry.getValue().getRelationInfo().getJoinModelInfo().getJoinModel()),
                jsonObject, pairs, operationListBuilder
            );
        }
    }

    private InsertOrUpdateOperation relation(Map.Entry<String, PropInfo> entry, ModelInfo modelInfo, JsonObject data, JsonObject jsonObject, Pairs pairs) {

        RelationTable relationTable = entry.getValue().getRelationInfo().getRelationTable();

        Object modelId = data.getValue(modelInfo.getPrimaryKey());
        Object childModelId = jsonObject.getValue(entry.getValue().getRelationInfo().getJoinModelInfo().getJoinField());

        return new InsertOrUpdateOperationBuilder()
            .setInsert(
                pairs.getRelationTableIdPairs().contains(
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

    @Override
    public Promise<List<JsonObject>> insertOrUpdateAll(String model, List<JsonObject> jsonObjects) {
        return null;
    }

    @Override
    public <T> Promise<T> delete(String model, T id) {
        return null;
    }

    @Override
    public <T> Promise<List<T>> deleteAll(String model, List<T> ids) {
        return null;
    }

    @Override
    public Promise<Long> count(String model) {
        return null;
    }

    @Override
    public Promise<Long> count(String model, JsonObject criteria) {
        return null;
    }

    @Override
    public Promise<List<JsonObject>> findAll(String model, JsonObject criteria) {
        return null;
    }

    @Override
    public Promise<List<JsonObject>> findAll(String model, JsonObject criteria, List<FieldInfo> selectFields) {
        return null;
    }

    private MapHandler<JsonArray, JsonObject> toJsonObject(List<FieldDetails> fieldDetailsList) {
        return ja -> convertToJa(ja, fieldDetailsList);
    }

    private JsonObject convertToJa(JsonArray jsonArray, List<FieldDetails> fieldDetailsList) {

        HashMap<String, Object> hashMap = new HashMap<>();

        final PathMap pathMap = new PathMap(hashMap);

        int idx = 0;
        for (FieldDetails fieldDetails : fieldDetailsList) {

            Map<String, Object> obj = pathMap.get(fieldDetails.path);

            for (String field : fieldDetails.fields) {

                obj.put(field, jsonArray.getValue(idx++));
            }
        }

        return new JsonObject(hashMap);
    }

    private List<FieldDetails> toFieldDetailsList(List<FieldInfo> selectFields) {
        ImmutableList.Builder<FieldDetails> fieldListBuilder = ImmutableList.builder();

        selectFields.forEach(fieldInfo -> {
            fieldListBuilder.add(new FieldDetails(toParts(fieldInfo.getPath()), fieldInfo.getFields()));
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

            fieldInfo.fields.forEach(field -> {
                listBuilder.add(path.get(field));
            });
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
        final List<String> path;
        final List<String> fields;

        private FieldDetails(List<String> path, List<String> fields) {
            this.path = path;
            this.fields = fields;
        }
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
