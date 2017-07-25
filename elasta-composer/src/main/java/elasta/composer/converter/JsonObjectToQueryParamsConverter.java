package elasta.composer.converter;

import elasta.composer.model.PageRequest;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
@FunctionalInterface
public interface JsonObjectToQueryParamsConverter extends Converter<JsonObjectToQueryParamsConverter.ConvertParams, QueryExecutor.QueryParams> {

    @Override
    QueryExecutor.QueryParams convert(ConvertParams params);

    @Value
    @Builder
    final class ConvertParams {
        final Collection<FieldExpression> selections;
        final String entity;
        final JsonObject query;
        final FieldExpression paginationKey;
        final PageRequest pageRequest;
        final String alias;
        private Collection<QueryExecutor.JoinParam> joinParams;

        ConvertParams(Collection<FieldExpression> selections, String entity, JsonObject query, FieldExpression paginationKey, PageRequest pageRequest, String alias, Collection<QueryExecutor.JoinParam> joinParams) {
            Objects.requireNonNull(selections);
            Objects.requireNonNull(entity);
            Objects.requireNonNull(query);
            Objects.requireNonNull(paginationKey);
            Objects.requireNonNull(pageRequest);
            Objects.requireNonNull(alias);
            Objects.requireNonNull(joinParams);
            this.selections = selections;
            this.entity = entity;
            this.query = query;
            this.paginationKey = paginationKey;
            this.pageRequest = pageRequest;
            this.alias = alias;
            this.joinParams = joinParams;
        }
    }
}
