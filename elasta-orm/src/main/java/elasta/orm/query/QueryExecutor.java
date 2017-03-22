package elasta.orm.query;

import elasta.core.promise.intfs.Promise;
import elasta.orm.query.expression.core.Order;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Value;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 3/19/2017.
 */
public interface QueryExecutor {
    
    Promise<List<JsonObject>> query(
        QueryParams params
    );

    Promise<List<JsonArray>> queryArray(
        QueryArrayParams params
    );

    @Value
    final class QueryArrayParams {
        final String entity;
        final JsonObject criteria;
        final List<JsonObject> selections;
        final List<OrderTpl> orderBy;
        final List<String> groupBy;
        final JsonObject having;

        public QueryArrayParams(String entity, JsonObject criteria, List<JsonObject> selections, List<OrderTpl> orderBy, List<String> groupBy, JsonObject having) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(criteria);
            Objects.requireNonNull(selections);
            Objects.requireNonNull(orderBy);
            Objects.requireNonNull(groupBy);
            Objects.requireNonNull(having);
            this.entity = entity;
            this.criteria = criteria;
            this.selections = selections;
            this.orderBy = orderBy;
            this.groupBy = groupBy;
            this.having = having;
        }
    }

    @Value
    final class QueryParams {
        final String entity;
        final JsonObject criteria;
        final List<String> selections;
        final List<OrderTpl> orderBy;
        final List<String> groupBy;
        final JsonObject having;

        public QueryParams(String entity, JsonObject criteria, List<String> selections, List<OrderTpl> orderBy, List<String> groupBy, JsonObject having) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(criteria);
            Objects.requireNonNull(selections);
            Objects.requireNonNull(orderBy);
            Objects.requireNonNull(groupBy);
            Objects.requireNonNull(having);
            this.entity = entity;
            this.criteria = criteria;
            this.selections = selections;
            this.orderBy = orderBy;
            this.groupBy = groupBy;
            this.having = having;
        }
    }

    /**
     * Created by sohan on 3/21/2017.
     */
    @Value
    final class OrderTpl {
        final String field;
        final Order order;

        public OrderTpl(String field, Order order) {
            Objects.requireNonNull(field);
            Objects.requireNonNull(order);
            this.field = field;
            this.order = order;
        }
    }
}
