package elasta.orm.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.core.promise.intfs.Promise;
import elasta.sql.JsonOps;
import elasta.orm.*;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.ex.OrmException;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import elasta.sql.core.UpdateTpl;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/22/2017.
 */
final public class OrmImpl implements Orm {
    final EntityMappingHelper helper;
    final BaseOrm baseOrm;
    final QueryDataLoader queryDataLoader;

    public OrmImpl(EntityMappingHelper helper, BaseOrm baseOrm, QueryDataLoader queryDataLoader) {
        Objects.requireNonNull(helper);
        Objects.requireNonNull(baseOrm);
        Objects.requireNonNull(queryDataLoader);
        this.helper = helper;
        this.baseOrm = baseOrm;
        this.queryDataLoader = queryDataLoader;
    }

    @Override
    public Promise<Long> countDistinct(String entity) {
        return countDistinct(entity, "r", OrmUtils.emptyJsonObject());
    }

    @Override
    public Promise<Long> countDistinct(String entity, String alias, JsonObject criteria) {
        return baseOrm.queryArray(
            QueryExecutor.QueryArrayParams.builder()
                .alias(alias)
                .entity(entity)
                .joinParams(ImmutableList.of())
                .criteria(criteria)
                .having(OrmUtils.emptyJsonObject())
                .groupBy(ImmutableList.of())
                .orderBy(ImmutableList.of())
                .selections(ImmutableList.of(
                    JsonOps.countDistinct(alias + "." + helper.getPrimaryKey(entity))
                ))
                .build()
        ).map(jsonArrays -> jsonArrays.get(0).getLong(0));
    }

    @Override
    public Promise<Long> countDistinct(CountDistinctParams params) {
        return baseOrm.queryArray(
            QueryExecutor.QueryArrayParams.builder()
                .alias(params.getAlias())
                .entity(params.getEntity())
                .joinParams(params.getJoinParams())
                .criteria(params.getCriteria())
                .having(params.getHaving())
                .groupBy(params.getGroupBy())
                .orderBy(ImmutableList.of())
                .selections(ImmutableList.of(
                    JsonOps.countDistinct(
                        params.getCountingKey()
                            .map(Object::toString)
                            .orElseGet(() -> params.getAlias() + "." + helper.getPrimaryKey(params.getEntity()))
                    )
                ))
                .build()
        ).map(jsonArrays -> jsonArrays.get(0).getLong(0));
    }

    @Override
    public <T> Promise<JsonObject> findOne(String entity, String alias, T id, Collection<FieldExpression> selections) {
        return queryDataLoader.query(
            QueryExecutor.QueryParams.builder()
                .entity(entity)
                .alias(alias)
                .joinParams(ImmutableList.of())
                .criteria(
                    JsonOps.eq(
                        alias + "." + helper.getPrimaryKey(entity), id
                    )
                )
                .having(OrmUtils.emptyJsonObject())
                .groupBy(ImmutableList.of())
                .orderBy(ImmutableList.of())
                .selections(selections)
                .build()
        ).map(jsonObjects -> {
            if (jsonObjects.size() <= 0) {
                throw new OrmException("No Entity found for id '" + id + "'");
            }
            return jsonObjects.get(0);
        });
    }

    @Override
    public <T> Promise<JsonObject> findOne(String entity, String alias, JsonObject criteria, Collection<FieldExpression> selections) {
        return findAll(entity, alias, criteria, selections)
            .map(jsonObjects -> {
                if (jsonObjects.size() > 1) {
                    throw new OrmException("Multiple results found but expected one");
                }
                if (jsonObjects.size() <= 0) {
                    throw new OrmException("No result found");
                }

                return jsonObjects.get(0);
            });
    }

    @Override
    public <T> Promise<List<JsonObject>> findAll(String entity, String alias, Collection<T> ids, Collection<FieldExpression> selections) {
        return queryDataLoader.query(
            QueryExecutor.QueryParams.builder()
                .entity(entity)
                .alias(alias)
                .joinParams(ImmutableList.of())
                .criteria(
                    JsonOps.in(alias + "." + helper.getPrimaryKey(entity), ids)
                )
                .having(OrmUtils.emptyJsonObject())
                .groupBy(ImmutableList.of())
                .orderBy(ImmutableList.of())
                .selections(selections)
                .build()
        );
    }

    @Override
    public Promise<List<JsonObject>> findAll(String entity, String alias, JsonObject criteria, Collection<FieldExpression> selections) {
        return queryDataLoader.query(
            QueryExecutor.QueryParams.builder()
                .entity(entity)
                .alias(alias)
                .joinParams(ImmutableList.of())
                .criteria(criteria)
                .having(OrmUtils.emptyJsonObject())
                .groupBy(ImmutableList.of())
                .orderBy(ImmutableList.of())
                .selections(selections)
                .build()
        );
    }

    @Override
    public Promise<List<JsonObject>> findAll(QueryExecutor.QueryParams params) {
        return queryDataLoader.query(params);
    }

    @Override
    public Promise<List<JsonArray>> queryArray(QueryExecutor.QueryArrayParams params) {
        return baseOrm.queryArray(params);
    }

    @Override
    public <T> Promise<T> querySingle(QueryExecutor.QueryArrayParams params) {
        return baseOrm.queryArray(params).map(jsonArrays -> {
            if (jsonArrays.size() <= 0) {
                throw new OrmException("Multiple Result exists, expected single result");
            }
            return (T) jsonArrays.get(0).getValue(0);
        });
    }

    @Override
    public Promise<List<UpdateTpl>> upsert(String entity, JsonObject jsonObject) {
        return baseOrm.upsert(
            BaseOrm.UpsertParams.builder()
                .entity(entity)
                .jsonObject(jsonObject)
                .build()
        );
    }

    @Override
    public <T extends Collection<JsonObject>> Promise<List<UpdateTpl>> upsertAll(String entity, T jsonObjects) {
        return baseOrm.execute(

            jsonObjects.stream()
                .map(jsonObject -> BaseOrm.ExecuteParams.builder()
                    .operationType(BaseOrm.OperationType.UPSERT)
                    .entity(entity)
                    .jsonObject(jsonObject)
                    .build())
                .collect(Collectors.toList())

        );
    }

    @Override
    public <T> Promise<List<UpdateTpl>> delete(String entity, T id) {
        return baseOrm.delete(
            BaseOrm.DeleteParams.builder()
                .entity(entity)
                .jsonObject(
                    new JsonObject(
                        ImmutableMap.of(
                            helper.getPrimaryKey(entity), id
                        )
                    )
                )
                .build()
        );
    }

    @Override
    public <I, T extends Collection<I>> Promise<List<UpdateTpl>> deleteAll(String entity, T ids) {
        return baseOrm.execute(
            ids.stream()
                .map(
                    id -> BaseOrm.ExecuteParams.builder()
                        .operationType(BaseOrm.OperationType.DELETE)
                        .entity(entity)
                        .jsonObject(
                            new JsonObject(
                                ImmutableMap.of(
                                    helper.getPrimaryKey(entity), id
                                )
                            )
                        )
                        .build()
                )
                .collect(Collectors.toList())

        );
    }

    @Override
    public Promise<List<UpdateTpl>> deleteChildRelations(String entity, JsonObject jsonObject) {
        return baseOrm.deleteChildRelations(
            BaseOrm.DeleteChildRelationsParams.builder()
                .entity(entity)
                .jsonObject(jsonObject)
                .build()
        );
    }

    @Override
    public <T extends Collection<JsonObject>> Promise<List<UpdateTpl>> deleteAllChildRelations(String entity, T jsonObjects) {
        return baseOrm.execute(
            jsonObjects.stream()
                .map(
                    jsonObject -> BaseOrm.ExecuteParams.builder()
                        .operationType(BaseOrm.OperationType.DELETE_CHILD_RELATIONS)
                        .entity(entity)
                        .jsonObject(jsonObject)
                        .build()
                )
                .collect(Collectors.toList())
        );
    }

    @Override
    public Promise<List<UpdateTpl>> execute(BaseOrm.ExecuteParams params) {
        return baseOrm.execute(ImmutableList.of(params));
    }

    @Override
    public Promise<List<UpdateTpl>> executeAll(Collection<BaseOrm.ExecuteParams> paramss) {
        return baseOrm.execute(paramss);
    }
}
