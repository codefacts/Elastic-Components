package elasta.orm.query;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.core.promise.intfs.Promise;
import elasta.orm.OrmUtils;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.PathExpression;
import elasta.sql.core.Order;
import elasta.sql.core.JoinType;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import javax.swing.text.html.Option;
import java.util.*;

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
        final Collection<JoinParam> joinParams;
        final JsonObject criteria;
        final Collection<JsonObject> selections;
        final Collection<OrderTpl> orderBy;
        final Collection<FieldExpression> groupBy;
        final JsonObject having;
        final Pagination pagination;

        QueryArrayParams(String entity, String alias, Collection<JoinParam> joinParams, JsonObject criteria, Collection<JsonObject> selections, Collection<OrderTpl> orderBy, Collection<FieldExpression> groupBy, JsonObject having, Pagination pagination) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(alias);
            Objects.requireNonNull(selections);
            this.entity = entity;
            this.alias = alias;
            this.selections = selections;
            this.joinParams = joinParams == null ? ImmutableList.of() : joinParams;
            this.criteria = criteria == null ? OrmUtils.emptyJsonObject() : criteria;
            this.orderBy = orderBy == null ? ImmutableList.of() : orderBy;
            this.groupBy = groupBy == null ? ImmutableList.of() : groupBy;
            this.having = having == null ? OrmUtils.emptyJsonObject() : having;
            this.pagination = (pagination == null) ? null : pagination;
        }

        public Optional<Pagination> getPagination() {
            return Optional.ofNullable(pagination);
        }
    }

    @Value
    @Builder
    final class QueryParams {
        final String entity;
        final String alias;
        final Collection<JoinParam> joinParams;
        final JsonObject criteria;
        final Collection<FieldExpression> selections;
        final Collection<OrderTpl> orderBy;
        final Collection<FieldExpression> groupBy;
        final JsonObject having;
        final Pagination pagination;

        QueryParams(String entity, String alias, Collection<JoinParam> joinParams, JsonObject criteria, Collection<FieldExpression> selections, Collection<OrderTpl> orderBy, Collection<FieldExpression> groupBy, JsonObject having, Pagination pagination) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(alias);
            Objects.requireNonNull(selections);
            this.entity = entity;
            this.alias = alias;
            this.selections = selections;
            this.joinParams = joinParams == null ? ImmutableList.of() : joinParams;
            this.criteria = criteria == null ? OrmUtils.emptyJsonObject() : criteria;
            this.orderBy = orderBy == null ? ImmutableList.of() : orderBy;
            this.groupBy = groupBy == null ? ImmutableList.of() : groupBy;
            this.having = having == null ? OrmUtils.emptyJsonObject() : having;
            this.pagination = (pagination == null) ? null : pagination;
        }

        public Optional<Pagination> getPagination() {
            return Optional.ofNullable(pagination);
        }
    }

    /**
     * Created by sohan on 3/21/2017.
     */
    @Value
    @Builder
    final class OrderTpl {
        final FieldExpression field;
        final Order order;

        public OrderTpl(FieldExpression field, Order order) {
            Objects.requireNonNull(field);
            Objects.requireNonNull(order);
            this.field = field;
            this.order = order;
        }
    }

    @Value
    @Builder
    final class JoinParam {
        final PathExpression path;
        final String alias;
        final JoinType joinType;

        public JoinParam(PathExpression path, String alias) {
            this(path, alias, null);
        }

        public JoinParam(PathExpression path, String alias, JoinType joinType) {
            Objects.requireNonNull(path);
            Objects.requireNonNull(alias);
            this.path = path;
            this.alias = alias;
            this.joinType = joinType == null ? null : joinType;
        }

        public Optional<JoinType> getJoinType() {
            return Optional.ofNullable(joinType);
        }
    }

    @Value
    @Builder
    final class Pagination {
        final FieldExpression fieldExpression;
        final long offset;
        final int size;

        Pagination(FieldExpression fieldExpression, long offset, int size) {
            Objects.requireNonNull(fieldExpression);
            Objects.requireNonNull(offset);
            Objects.requireNonNull(size);
            this.fieldExpression = fieldExpression;
            this.offset = offset;
            this.size = size;
        }
    }
}
