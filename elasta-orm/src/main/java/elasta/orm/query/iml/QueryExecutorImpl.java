package elasta.orm.query.iml;

import elasta.core.promise.intfs.Promise;
import elasta.criteria.json.mapping.JsonToFuncConverter;
import elasta.criteria.json.mapping.JsonToFuncConverterMap;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.expression.Query;
import elasta.orm.query.expression.builder.impl.QueryBuilderImpl;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import elasta.sql.SqlExecutor;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 3/19/2017.
 */
final public class QueryExecutorImpl implements QueryExecutor {
    final EntityMappingHelper helper;
    final JsonToFuncConverterMap jsonToFuncConverterMap;
    final JsonToFuncConverter jsonToFuncConverter;
    final SqlExecutor sqlExecutor;

    public QueryExecutorImpl(EntityMappingHelper helper, JsonToFuncConverterMap jsonToFuncConverterMap, JsonToFuncConverter jsonToFuncConverter, SqlExecutor sqlExecutor) {
        Objects.requireNonNull(helper);
        Objects.requireNonNull(jsonToFuncConverterMap);
        Objects.requireNonNull(jsonToFuncConverter);
        Objects.requireNonNull(sqlExecutor);
        this.helper = helper;
        this.jsonToFuncConverterMap = jsonToFuncConverterMap;
        this.jsonToFuncConverter = jsonToFuncConverter;
        this.sqlExecutor = sqlExecutor;
    }

    @Override
    public Promise<List<JsonObject>> query(QueryParams params) {
        return doQuery(params);
    }

    private Promise<List<JsonObject>> doQuery(QueryParams params) {
        QueryBuilderImpl qb = new QueryBuilderImpl(
            helper,
            sqlExecutor
        );

        params.getSelections().forEach(field -> qb.selectBuilder().add(
            qb.select(field)
        ));

        return prepareAndExecute(
            qb, Params.builder()
                .entity(params.getEntity())
                .alias(params.getAlias())
                .joinParams(params.getJoinParams())
                .criteria(params.getCriteria())
                .groupBy(params.getGroupBy())
                .orderBy(params.getOrderBy())
                .having(params.getHaving())
                .build()
        ).execute();
    }

    @Override
    public Promise<List<JsonArray>> queryArray(QueryArrayParams params) {
        QueryBuilderImpl qb = new QueryBuilderImpl(
            helper,
            sqlExecutor
        );


        return prepareAndExecute(
            qb, Params.builder()
                .entity(params.getEntity())
                .alias(params.getAlias())
                .joinParams(params.getJoinParams())
                .criteria(params.getCriteria())
                .groupBy(params.getGroupBy())
                .orderBy(params.getOrderBy())
                .having(params.getHaving())
                .build()
        ).executeArray();
    }

    private Query prepareAndExecute(QueryBuilderImpl qb, Params params) {

        qb.fromBuilder().root(params.getEntity(), params.getAlias());

        params.getJoinParams().forEach(joinParam -> {
            qb.fromBuilder().join(
                PathExpression.parseAndCreate(
                    joinParam.getPath()
                ),
                joinParam.getAlias(),
                joinParam.getJoinType()
            );
        });

        final CriteriaBuilderJsonToFuncConverterMap converterMap = new CriteriaBuilderJsonToFuncConverterMap(jsonToFuncConverterMap, qb);

        qb.whereBuilder().add(
            jsonToFuncConverter.convert(params.getCriteria(), converterMap)
        );

        params.getOrderBy().forEach(orderTpl -> {
            qb.orderByBuilder().add(
                new FieldExpressionImpl(orderTpl.getField()), orderTpl.getOrder()
            );
        });

        params.getGroupBy().forEach(field -> {
            qb.groupByBuilder().add(
                new FieldExpressionImpl(field)
            );
        });

        qb.havingBuilder().add(
            jsonToFuncConverter.convert(params.getHaving(), converterMap)
        );

        return qb.build();
    }

    @Value
    @Builder
    private static final class Params {
        final String entity;
        final String alias;
        final List<JoinParam> joinParams;
        final JsonObject criteria;
        final List<OrderTpl> orderBy;
        final List<String> groupBy;
        final JsonObject having;
    }
}
