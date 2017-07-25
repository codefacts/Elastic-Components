package tracker;

import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import lombok.Builder;
import lombok.Value;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 6/30/2017.
 */
public interface EntityToDefaultSelectionsMap {

    SelectionsAndJoinParams fieldSelections(EntityAndAction entityAndAction);

    @Value
    @Builder
    final class EntityAndAction {
        final String entity;
        final String action;

        public EntityAndAction(String entity, String action) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(action);
            this.entity = entity;
            this.action = action;
        }
    }

    @Value
    @Builder
    final class SelectionsAndJoinParams {
        final List<FieldExpression> fieldExpressions;
        final List<QueryExecutor.JoinParam> joinParams;

        public SelectionsAndJoinParams(List<FieldExpression> fieldExpressions, List<QueryExecutor.JoinParam> joinParams) {
            Objects.requireNonNull(fieldExpressions);
            Objects.requireNonNull(joinParams);
            this.fieldExpressions = fieldExpressions;
            this.joinParams = joinParams;
        }
    }
}
