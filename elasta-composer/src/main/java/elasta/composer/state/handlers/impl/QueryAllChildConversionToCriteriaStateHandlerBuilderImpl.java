package elasta.composer.state.handlers.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.model.request.QueryChildModel;
import elasta.composer.state.handlers.QueryAllChildConversionToCriteriaStateHandlerBuilder;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;
import elasta.criteria.json.mapping.JsonOps;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 5/29/2017.
 */
final public class QueryAllChildConversionToCriteriaStateHandlerBuilderImpl implements QueryAllChildConversionToCriteriaStateHandlerBuilder {
    final String parentPrimaryKeyExpStr;
    final String parentToChildPathExpStr;

    public QueryAllChildConversionToCriteriaStateHandlerBuilderImpl(String parentPrimaryKeyExpStr, String parentToChildPathExpStr) {
        Objects.requireNonNull(parentPrimaryKeyExpStr);
        Objects.requireNonNull(parentToChildPathExpStr);
        this.parentPrimaryKeyExpStr = parentPrimaryKeyExpStr;
        this.parentToChildPathExpStr = parentToChildPathExpStr;
    }

    @Override
    public MsgEnterEventHandlerP<JsonObject, JsonObject> build() {
        return msg -> {

            final JsonObject body = msg.body();

            Objects.requireNonNull(body);

            final Object parentId = body.getValue(QueryChildModel.parentId);

            Objects.requireNonNull(parentId);

            final JsonObject criteria = body.getJsonObject(QueryChildModel.criteria);

            return Promises.of(
                Flow.trigger(
                    Events.next,
                    msg.withBody(
                        JsonOps.and(
                            ImmutableList.of(
                                JsonOps.eq(parentPrimaryKeyExpStr, parentId),
                                replaceFieldRecursive(criteria)
                            )
                        )
                    )
                )
            );
        };
    }

    private JsonObject replaceFieldRecursive(JsonObject criteria) {

        final String op = criteria.getString(JsonOps.op);

        if (Objects.equals(op, JsonOps.field)) {
            String fieldExp = criteria.getString(JsonOps.arg);
            return JsonOps.field(
                PathExpression.parseAndCreate(parentToChildPathExpStr)
                    .concat(
                        new FieldExpressionImpl(fieldExp).toPathExpression().subPath(1)
                    )
                    .toString()
            );
        }

        final ImmutableMap.Builder<String, Object> mapBuilder = ImmutableMap.builder();

        for (Map.Entry<String, Object> entry : criteria) {

            final Object value = entry.getValue();

            mapBuilder.put(entry.getKey(), handleValue(value));
        }

        return new JsonObject(mapBuilder.build());
    }

    private Object handleValue(Object value) {

        if (value instanceof JsonObject || value instanceof Map) {

            return replaceFieldRecursive(
                value instanceof JsonObject
                    ? (JsonObject) value
                    : new JsonObject(castToMap(value))
            );

        } else if (value instanceof JsonArray || value instanceof List) {

            return handleJsonArray(
                value instanceof JsonArray
                    ? ((JsonArray) value)
                    : new JsonArray(castToList(value))
            );
        }

        return value;
    }

    private JsonArray handleJsonArray(JsonArray jsonArray) {

        final ImmutableList.Builder<Object> listBuilder = ImmutableList.builder();

        for (int i = 0; i < jsonArray.size(); i++) {

            listBuilder.add(
                handleValue(
                    jsonArray.getValue(i)
                )
            );
        }

        return new JsonArray(listBuilder.build());
    }

    private List castToList(Object value) {
        return ((List) value);
    }

    private Map<String, Object> castToMap(Object value) {
        return ((Map<String, Object>) value);
    }
}
