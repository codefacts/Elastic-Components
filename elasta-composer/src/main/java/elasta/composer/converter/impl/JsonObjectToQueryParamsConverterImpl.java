package elasta.composer.converter.impl;

import com.google.common.collect.ImmutableList;
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
        
        return QueryExecutor.QueryParams.builder()
            .entity(params.getEntity())
            .alias(query.getString(QueryModel.alias))
            .joinParams(toJoinParams(query.getJsonArray(QueryModel.joinParams)))
            .selections(toSelections(query.getJsonArray(QueryModel.selections)))
            .criteria(query.getJsonObject(QueryModel.criteria))
            .having(query.getJsonObject(QueryModel.having))
            .groupBy(toGroupBy(query.getJsonArray(QueryModel.groupBy)))
            .orderBy(toOrderBy(query.getJsonArray(QueryModel.orderBy)))
            .pagination(toPagination(params.getPageRequest(), params.getPaginationKey()))
            .build();
    }

    private QueryExecutor.Pagination toPagination(PageRequest pageRequest, FieldExpression paginationKey) {

        int size = pageRequest.getPageSize();

        return QueryExecutor.Pagination.builder()
            .fieldExpression(paginationKey)
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

    private Collection<FieldExpression> toSelections(JsonArray jsonArray) {
        ImmutableList.Builder<FieldExpression> selectionsBuilder = ImmutableList.builder();
        for (int i = 0; i < jsonArray.size(); i++) {
            selectionsBuilder.add(
                new FieldExpressionImpl(jsonArray.getString(i))
            );
        }
        return selectionsBuilder.build();
    }
}
