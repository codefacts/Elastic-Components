package elasta.orm.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.BaseOrm;
import elasta.orm.delete.DeleteFunction;
import elasta.orm.delete.impl.ColumnValuePair;
import elasta.orm.delete.impl.DeleteContextImpl;
import elasta.orm.delete.impl.DeleteData;
import elasta.orm.event.dbaction.DbInterceptors;
import elasta.orm.ex.OrmException;
import elasta.orm.query.QueryExecutor;
import elasta.orm.relation.delete.DeleteChildRelationsFunction;
import elasta.orm.relation.delete.impl.DeleteChildRelationsContextImpl;
import elasta.orm.relation.delete.impl.DeleteRelationData;
import elasta.sql.core.*;
import elasta.orm.upsert.TableData;
import elasta.orm.upsert.impl.UpsertContextImpl;
import elasta.orm.upsert.UpsertFunction;
import elasta.sql.SqlDB;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Value;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/19/2017.
 */
final public class BaseOrmImpl implements BaseOrm {
    static final JsonObject EMPTY_JSON_OBJECT = new JsonObject(ImmutableMap.of());
    final Map<String, EntityOperation> operationMap;
    final QueryExecutor queryExecutor;
    final SqlDB sqlDB;
    final DbInterceptors dbInterceptors;

    public BaseOrmImpl(Map<String, EntityOperation> operationMap, QueryExecutor queryExecutor, SqlDB sqlDB, DbInterceptors dbInterceptors) {
        this.dbInterceptors = dbInterceptors;
        Objects.requireNonNull(operationMap);
        Objects.requireNonNull(queryExecutor);
        Objects.requireNonNull(sqlDB);
        this.operationMap = operationMap;
        this.queryExecutor = queryExecutor;
        this.sqlDB = sqlDB;
    }

    @Override
    public Promise<JsonObject> upsert(UpsertParams params) {
        return doUpsert(params)
            .thenP(sqlDB::update)
            .map(updateTpls -> params.getJsonObject());
    }

    @Override
    public Promise<JsonObject> delete(DeleteParams params) {
        return doDelete(params)
            .thenP(sqlDB::update)
            .map(updateTplList -> params.getJsonObject());
    }

    @Override
    public Promise<List<JsonObject>> query(QueryExecutor.QueryParams params) {
        return queryExecutor.query(params);
    }

    @Override
    public Promise<List<JsonArray>> queryArray(QueryExecutor.QueryArrayParams params) {
        return queryExecutor.queryArray(params);
    }

    @Override
    public Promise<JsonObject> deleteChildRelations(DeleteChildRelationsParams params) {
        return doDeleteChildRelations(params)
            .mapP(sqlDB::update)
            .map(aVoid -> params.getJsonObject())
            ;
    }

    @Override
    public Promise<Void> execute(Collection<ExecuteParams> paramss) {

        return doExecute(paramss)
            .map(sqlDB::update)
            .map(voidPromise -> null);
    }

    private Promise<List<UpdateTpl>> doUpsert(UpsertParams params) {
        LinkedHashMap<String, TableData> map = new LinkedHashMap<>();

        getOperation(params.getEntity()).upsertFunction.upsert(
            params.getJsonObject(),
            new UpsertContextImpl(
                map
            )
        );

        List<Promise<UpdateTpl>> promiseList = map.values().stream()
            .map(tableData -> {

                final List<SqlCriteria> sqlCriterias = Arrays.stream(tableData.getPrimaryColumns())
                    .map(column -> new SqlCriteria(column, tableData.getValues().getValue(column), Optional.empty()))
                    .collect(Collectors.toList());

                return sqlDB
                    .exists(
                        tableData.getTable(),
                        tableData.getPrimaryColumns()[0],
                        sqlCriterias
                    )
                    .map(exists -> UpdateTpl.builder()
                        .updateOperationType(exists ? UpdateOperationType.UPDATE : UpdateOperationType.INSERT)
                        .table(tableData.getTable())
                        .sqlCriterias(exists ? sqlCriterias : ImmutableList.of())
                        .data(tableData.getValues())
                        .build())
                    .mapP(dbInterceptors::interceptUpdateTpl)
                    ;

            }).collect(Collectors.toList());

        return Promises.when(promiseList);
    }

    private Promise<List<UpdateTpl>> doDelete(DeleteParams params) {
        LinkedHashSet<DeleteData> deleteDatas = new LinkedHashSet<>();
        return getOperation(params.getEntity()).deleteFunction
            .delete(
                params.getJsonObject(), new DeleteContextImpl(
                    deleteDatas
                )
            )
            .mapP(jsonObject -> {

                List<Promise<UpdateTpl>> promiseList = deleteDatas.stream()
                    .map(
                        deleteData ->
                            deleteData.getOperationType() == DeleteData.OperationType.UPDATE
                                ?
                                UpdateTpl.builder()
                                    .updateOperationType(UpdateOperationType.UPDATE)
                                    .table(deleteData.getTable())
                                    .data(toJsonObject(deleteData.getUpdateValues()))
                                    .sqlCriterias(toSqlCriterias(Arrays.asList(deleteData.getWhereCriteria())))
                                    .build()
                                :
                                UpdateTpl.builder()
                                    .updateOperationType(UpdateOperationType.DELETE)
                                    .table(deleteData.getTable())
                                    .data(EMPTY_JSON_OBJECT)
                                    .sqlCriterias(toSqlCriterias(Arrays.asList(deleteData.getWhereCriteria())))
                                    .build()
                    )
                    .map(dbInterceptors::interceptUpdateTpl)
                    .collect(Collectors.toList());

                return Promises.when(promiseList);
            });
    }

    private Promise<List<UpdateTpl>> doDeleteChildRelations(DeleteChildRelationsParams params) {
        EntityOperation operation = getOperation(params.getEntity());

        Set<DeleteRelationData> deleteRelationDataSet = new LinkedHashSet<>();

        operation.getDeleteChildRelationsFunction()
            .deleteChildRelations(
                params.getJsonObject(), new DeleteChildRelationsContextImpl(deleteRelationDataSet)
            )
        ;

        List<Promise<UpdateTpl>> promiseList = deleteRelationDataSet.stream()
            .map(
                deleteRelationData -> new UpdateTpl(
                    UpdateOperationType.valueOf(deleteRelationData.getOperationType().name()),
                    deleteRelationData.getReferencingTable(),
                    new JsonObject(
                        deleteRelationData.getReferencingColumns().stream()
                            .collect(Collectors.toMap(
                                column -> column,
                                column -> null
                            ))
                    ),
                    toSqlCriterias(deleteRelationData.getPrimaryColumnValuePairs())
                )
            )
            .map(dbInterceptors::interceptUpdateTpl)
            .collect(Collectors.toList());

        return Promises.when(promiseList);
    }

    private Promise<List<UpdateTpl>> doExecute(Collection<ExecuteParams> paramss) {
        List<Promise<List<UpdateTpl>>> promiseList = paramss.stream()
            .map(params -> {
                switch (params.getOperationType()) {
                    case UPSERT: {
                        return doUpsert(
                            new UpsertParams(params.getEntity(), params.getJsonObject())
                        );
                    }
                    case DELETE: {
                        return doDelete(
                            new DeleteParams(params.getEntity(), params.getJsonObject())
                        );
                    }
                    case DELETE_CHILD_RELATIONS: {
                        return doDeleteChildRelations(
                            new DeleteChildRelationsParams(params.getEntity(), params.getJsonObject())
                        );
                    }
                }
                throw new OrmException("Invalid OperationType '" + params.getOperationType() + "' in ExecuteParams");
            })
            .collect(Collectors.toList());

        return Promises.when(promiseList)
            .map(lists -> lists.stream().flatMap(Collection::stream).collect(Collectors.toList()));
    }

    private JsonObject toJsonObject(DeleteData.ColumnAndOptionalValuePair[] updateValues) {
        JsonObject jsonObject = new JsonObject();
        for (DeleteData.ColumnAndOptionalValuePair updateValue : updateValues) {

            Object value = updateValue.getValue().isPresent() ? updateValue.getValue().get() : null;

            jsonObject.put(updateValue.getColumn(), value);
        }
        return jsonObject;
    }

    private Collection<SqlCriteria> toSqlCriterias(List<ColumnValuePair> columnValuePairs) {
        ImmutableList.Builder<SqlCriteria> sqlCriteriaListBuilder = ImmutableList.builder();

        columnValuePairs.stream()
            .map(
                columnValuePair -> new SqlCriteria(
                    columnValuePair.getPrimaryColumn(),
                    columnValuePair.getValue(),
                    Optional.empty()
                )
            )
            .forEach(sqlCriteriaListBuilder::add);

        return sqlCriteriaListBuilder.build();
    }

    private EntityOperation getOperation(String entity) {
        EntityOperation entityOperation = operationMap.get(entity);
        if (entityOperation == null) {
            throw new OrmException("No Entity Operation found in the operationMap for rootEntity '" + entity + "'");
        }
        return entityOperation;
    }

    @Value
    public static final class EntityOperation {
        final UpsertFunction upsertFunction;
        final DeleteFunction deleteFunction;
        final DeleteChildRelationsFunction deleteChildRelationsFunction;

        public EntityOperation(UpsertFunction upsertFunction, DeleteFunction deleteFunction, DeleteChildRelationsFunction deleteChildRelationsFunction) {
            Objects.requireNonNull(upsertFunction);
            Objects.requireNonNull(deleteFunction);
            Objects.requireNonNull(deleteChildRelationsFunction);
            this.upsertFunction = upsertFunction;
            this.deleteFunction = deleteFunction;
            this.deleteChildRelationsFunction = deleteChildRelationsFunction;
        }
    }

    public static void main(String[] asdf) {

    }
}
