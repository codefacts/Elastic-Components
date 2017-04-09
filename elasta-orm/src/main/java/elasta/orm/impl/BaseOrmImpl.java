package elasta.orm.impl;

import com.google.common.collect.ImmutableList;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.BaseOrm;
import elasta.orm.delete.DeleteFunction;
import elasta.orm.delete.impl.DeleteContextImpl;
import elasta.orm.event.dbaction.DbInterceptors;
import elasta.orm.ex.OrmException;
import elasta.orm.query.QueryExecutor;
import elasta.orm.upsert.TableData;
import elasta.orm.upsert.UpsertContextImpl;
import elasta.orm.upsert.UpsertFunction;
import elasta.sql.SqlDB;
import elasta.sql.core.DeleteData;
import elasta.sql.core.SqlCriteria;
import elasta.sql.core.UpdateOperationType;
import elasta.sql.core.UpdateTpl;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Value;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/19/2017.
 */
final public class BaseOrmImpl implements BaseOrm {
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
            .map(tableData -> dbInterceptors.interceptTableData(tableData)
                .mapP(tableData1 -> {

                    final List<SqlCriteria> sqlCriterias = Arrays.stream(tableData.getPrimaryColumns())
                        .map(column -> new SqlCriteria(column, tableData.getValues().getValue(column), Optional.empty()))
                        .collect(Collectors.toList());

                    return sqlDB.exists(
                        tableData.getTable(),
                        tableData.getPrimaryColumns()[0],
                        sqlCriterias
                    ).map(exists -> UpdateTpl.builder()
                        .updateOperationType(exists ? UpdateOperationType.UPDATE : UpdateOperationType.INSERT)
                        .table(tableData.getTable())
                        .sqlCriterias(sqlCriterias)
                        .data(tableData.getValues())
                        .build());
                })).collect(Collectors.toList());

        return Promises.when(promiseList)
            .mapP(updateTpls -> Promises.when(
                updateTpls.stream().map(dbInterceptors::interceptUpdateTpl)
                    .collect(Collectors.toList())
            ))
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
            ).thenP(jsonObject -> sqlDB.delete(deleteDatas));
    }

    private EntityOperation getOperation(String entity) {
        EntityOperation entityOperation = operationMap.get(entity);
        if (entityOperation == null) {
            throw new OrmException("No Entity Operation found in the operationMap for entity '" + entity + "'");
        }
        return entityOperation;
    }

    @Override
    public Promise<List<JsonObject>> query(QueryExecutor.QueryParams params) {
        return queryExecutor.query(params);
    }

    @Override
    public Promise<List<JsonArray>> queryArray(QueryExecutor.QueryArrayParams params) {
        return queryExecutor.queryArray(params);
    }

    @Value
    public static final class EntityOperation {
        final UpsertFunction upsertFunction;
        final DeleteFunction deleteFunction;

        public EntityOperation(UpsertFunction upsertFunction, DeleteFunction deleteFunction) {
            Objects.requireNonNull(upsertFunction);
            Objects.requireNonNull(deleteFunction);
            this.upsertFunction = upsertFunction;
            this.deleteFunction = deleteFunction;
        }
    }
}
