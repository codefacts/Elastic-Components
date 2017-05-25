package elasta.composer;

import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.PathExpression;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 5/25/2017.
 */
public interface NestedResourcePathTranslator {

    QueryParamsAndFullPath translate(String rootEntity, String nestedResourcePath);

    @Value
    @Builder
    final class QueryParamsAndFullPath {
        final QueryExecutor.QueryParams queryParams;
        final PathExpression fullPath;

        QueryParamsAndFullPath(QueryExecutor.QueryParams queryParams, PathExpression fullPath) {
            Objects.requireNonNull(queryParams);
            Objects.requireNonNull(fullPath);
            this.queryParams = queryParams;
            this.fullPath = fullPath;
        }
    }
}
