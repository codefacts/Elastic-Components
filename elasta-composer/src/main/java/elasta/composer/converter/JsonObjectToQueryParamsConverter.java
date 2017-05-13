package elasta.composer.converter;

import elasta.composer.model.PageRequest;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

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
        final String entity;
        final JsonObject query;
        final FieldExpression paginationKey;
        final PageRequest pageRequest;

        ConvertParams(String entity, JsonObject query, FieldExpression paginationKey, PageRequest pageRequest) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(query);
            Objects.requireNonNull(paginationKey);
            Objects.requireNonNull(pageRequest);
            this.entity = entity;
            this.query = query;
            this.paginationKey = paginationKey;
            this.pageRequest = pageRequest;
        }
    }
}
