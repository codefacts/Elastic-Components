package elasta.orm.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.BaseOrm;
import elasta.orm.delete.DeleteFunction;
import elasta.orm.delete.impl.DeleteContextImpl;
import elasta.orm.entity.EntityUtils;
import elasta.orm.event.dbaction.DbInterceptors;
import elasta.orm.ex.OrmException;
import elasta.orm.query.QueryExecutor;
import elasta.orm.relation.delete.DeleteChildRelationsFunction;
import elasta.orm.relation.delete.impl.DeleteChildRelationsContextImpl;
import elasta.sql.core.*;
import elasta.orm.upsert.TableData;
import elasta.orm.upsert.UpsertContextImpl;
import elasta.orm.upsert.UpsertFunction;
import elasta.sql.SqlDB;
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
                        .sqlCriterias(sqlCriterias)
                        .data(tableData.getValues())
                        .build())
                    .mapP(dbInterceptors::interceptUpdateTpl)
                    ;

            }).collect(Collectors.toList());

        return Promises.when(promiseList)
            .thenP(sqlDB::update)
            .map(updateTpls -> params.getJsonObject());
    }

    @Override
    public Promise<JsonObject> delete(DeleteParams params) {
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
                        deleteData -> UpdateTpl.builder()
                            .updateOperationType(UpdateOperationType.DELETE)
                            .table(deleteData.getTable())
                            .data(EMPTY_JSON_OBJECT)
                            .sqlCriterias(sqlCriterias(Arrays.asList(deleteData.getColumnValuePairs())))
                            .build()
                    )
                    .map(dbInterceptors::interceptUpdateTpl)
                    .collect(Collectors.toList());

                return Promises.when(promiseList);
            })
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
                    sqlCriterias(deleteRelationData.getPrimaryColumnValuePairs())
                )
            )
            .map(dbInterceptors::interceptUpdateTpl)
            .collect(Collectors.toList());

        return Promises.when(promiseList)
            .mapP(sqlDB::update)
            .map(aVoid -> params.getJsonObject())
            ;
    }

    private Collection<SqlCriteria> sqlCriterias(List<ColumnValuePair> columnValuePairs) {
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
            throw new OrmException("No Entity Operation found in the operationMap for entity '" + entity + "'");
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
