package elasta.composer.converter.impl;

import com.google.common.collect.ImmutableList;
import elasta.composer.ComposerUtils;
import elasta.composer.converter.JsonObjectToQueryParamsConverter;
import elasta.composer.model.request.JoinParamsModel;
import elasta.composer.model.request.OrderByModel;
import elasta.composer.model.PageRequest;
import elasta.composer.model.request.QueryModel;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import elasta.sql.core.JoinType;
import elasta.sql.core.Order;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.Objects;

import static elasta.composer.ComposerUtils.emptyJsonArray;

/**
 * Created by sohan on 5/12/2017.
 */
final public class JsonObjectToQueryParamsConverterImpl implements JsonObjectToQueryParamsConverter {

    public JsonObjectToQueryParamsConverterImpl() {
    }

    @Override
    public QueryExecutor.QueryParams convert(ConvertParams params) {
        Objects.requireNonNull(params);

        final JsonObject query = params.getQuery();

        Collection<FieldExpression> selections = params.getSelections();
        final Collection<FieldExpression> fieldExpressions = toSelections(query.getJsonArray(QueryModel.selections, emptyJsonArray()), selections);

        return QueryExecutor.QueryParams.builder()
            .entity(params.getEntity())
            .alias(params.getAlias())
            .joinParams(
                fieldExpressions == selections ? params.getJoinParams() : toJoinParams(query.getJsonArray(QueryModel.joinParams, emptyJsonArray()))
            )
            .selections(fieldExpressions)
            .criteria(query.getJsonObject(QueryModel.criteria, ComposerUtils.emptyJsonObject()))
            .having(query.getJsonObject(QueryModel.having, ComposerUtils.emptyJsonObject()))
            .groupBy(toGroupBy(query.getJsonArray(QueryModel.groupBy, emptyJsonArray())))
            .orderBy(toOrderBy(query.getJsonArray(QueryModel.orderBy, emptyJsonArray())))
            .pagination(
                toPagination(params.getPageRequest(), query.getString(QueryModel.paginationKey), params.getPaginationKey())
            )
            .build();
    }

    private QueryExecutor.Pagination toPagination(PageRequest pageRequest, String queryPaginationKey, FieldExpression paginationKey) {

        int size = pageRequest.getPageSize();

        return QueryExecutor.Pagination.builder()
            .fieldExpression((queryPaginationKey != null) ? new FieldExpressionImpl(queryPaginationKey) : paginationKey)
            .offset(offset(pageRequest.getPage(), size))
            .size(size)
            .build();
    }

    private long offset(long page, int pageSize) {
        return (page - 1) * pageSize;
    }

    private Collection<QueryExecutor.OrderTpl> toOrderBy(JsonArray jsonArray) {
        ImmutableList.Builder<QueryExecutor.OrderTpl> builder = ImmutableList.builder();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.getJsonObject(i);
            builder.add(
                QueryExecutor.OrderTpl.builder()
                    .field(
                        new FieldExpressionImpl(
                            jsonObject.getString(OrderByModel.field)
                        )
                    )
                    .order(
                        Order.valueOf(
                            jsonObject.getString(OrderByModel.order).toUpperCase()
                        )
                    )
                    .build()
            );
        }
        return builder.build();
    }

    private Collection<FieldExpression> toGroupBy(JsonArray jsonArray) {
        ImmutableList.Builder<FieldExpression> builder = ImmutableList.builder();
        for (int i = 0; i < jsonArray.size(); i++) {
            builder.add(
                new FieldExpressionImpl(
                    jsonArray.getString(i)
                )
            );
        }
        return builder.build();
    }

    private Collection<QueryExecutor.JoinParam> toJoinParams(JsonArray jsonArray) {
        ImmutableList.Builder<QueryExecutor.JoinParam> builder = ImmutableList.builder();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.getJsonObject(i);
            Objects.requireNonNull(jsonObject);
            builder.add(
                QueryExecutor.JoinParam.builder()
                    .joinType(JoinType.INNER_JOIN)
                    .alias(jsonObject.getString(JoinParamsModel.alias))
                    .path(PathExpression.parseAndCreate(
                        jsonObject.getString(JoinParamsModel.path)
                    ))
                    .build()
            );
        }
        return builder.build();
    }

    private Collection<FieldExpression> toSelections(JsonArray jsonArray, Collection<FieldExpression> selections) {

        if (jsonArray.isEmpty()) {
            return selections;
        }

        ImmutableList.Builder<FieldExpression> selectionsBuilder = ImmutableList.builder();
        for (int i = 0; i < jsonArray.size(); i++) {
            selectionsBuilder.add(
                new FieldExpressionImpl(jsonArray.getString(i))
            );
        }
        return selectionsBuilder.build();
    }
}
