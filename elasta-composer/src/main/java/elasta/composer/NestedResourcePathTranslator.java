package elasta.composer;

import elasta.composer.impl.NestedResourcePathTranslatorImpl;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.PathExpression;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 5/25/2017.
 */
public interface NestedResourcePathTranslator {

    QueryParamsAndFullPath translate(String rootEntity, String rootAlias, String nestedResourcePath);

    @Value
    @Builder
    final class QueryParamsAndFullPath {
        final List<FieldExpression> selections;
        final List<QueryExecutor.JoinParam> joins;
        final List<PathAndValue> criterias;
        final PathExpression fullPath;

        QueryParamsAndFullPath(List<FieldExpression> selections, List<QueryExecutor.JoinParam> joins, List<PathAndValue> criterias, PathExpression fullPath) {
            Objects.requireNonNull(selections);
            Objects.requireNonNull(joins);
            Objects.requireNonNull(criterias);
            Objects.requireNonNull(fullPath);
            this.selections = selections;
            this.joins = joins;
            this.criterias = criterias;
            this.fullPath = fullPath;
        }
    }

    @Value
    final class PathAndValue {
        final PathExpression pathExpression;
        final Object value;

        public PathAndValue(PathExpression pathExpression, Object value) {
            Objects.requireNonNull(pathExpression);
            Objects.requireNonNull(value);
            this.pathExpression = pathExpression;
            this.value = value;
        }
    }
}
