package elasta.orm.query;

import elasta.core.promise.intfs.Promise;
import elasta.sql.core.Order;
import elasta.sql.core.JoinType;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    @Builder
    final class QueryArrayParams {
        final String entity;
        final String alias;
        final List<JoinParam> joinParams;
        final JsonObject criteria;
        final List<JsonObject> selections;
        final List<OrderTpl> orderBy;
        final List<String> groupBy;
        final JsonObject having;

        public QueryArrayParams(String entity, String alias, List<JoinParam> joinParams, JsonObject criteria, List<JsonObject> selections, List<OrderTpl> orderBy, List<String> groupBy, JsonObject having) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(alias);
            Objects.requireNonNull(joinParams);
            Objects.requireNonNull(criteria);
            Objects.requireNonNull(selections);
            Objects.requireNonNull(orderBy);
            Objects.requireNonNull(groupBy);
            Objects.requireNonNull(having);
            this.entity = entity;
            this.alias = alias;
            this.joinParams = joinParams;
            this.criteria = criteria;
            this.selections = selections;
            this.orderBy = orderBy;
            this.groupBy = groupBy;
            this.having = having;
        }
    }

    @Value
    @Builder
    final class QueryParams {
        final String entity;
        final String alias;
        final List<JoinParam> joinParams;
        final JsonObject criteria;
        final List<String> selections;
        final List<OrderTpl> orderBy;
        final List<String> groupBy;
        final JsonObject having;

        public QueryParams(String entity, String alias, List<JoinParam> joinParams, JsonObject criteria, List<String> selections, List<OrderTpl> orderBy, List<String> groupBy, JsonObject having) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(alias);
            Objects.requireNonNull(joinParams);
            Objects.requireNonNull(criteria);
            Objects.requireNonNull(selections);
            Objects.requireNonNull(orderBy);
            Objects.requireNonNull(groupBy);
            Objects.requireNonNull(having);
            this.entity = entity;
            this.alias = alias;
            this.joinParams = joinParams;
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

    @Value
    @Builder
    final class JoinParam {
        final String path;
        final String alias;
        Optional<JoinType> joinType;
    }
}
