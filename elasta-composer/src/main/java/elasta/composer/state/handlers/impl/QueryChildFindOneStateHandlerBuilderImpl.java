package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.QueryChildFindOneStateHandlerBuilder;
import elasta.composer.state.handlers.ex.QueryChildFindOneStateHandlerException;
import elasta.core.flow.Flow;
import elasta.orm.Orm;
import elasta.orm.query.expression.FieldExpression;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 5/28/2017.
 */
final public class QueryChildFindOneStateHandlerBuilderImpl implements QueryChildFindOneStateHandlerBuilder {
    final String parentAlias;
    final String parentEntity;
    final String childField;
    final Collection<FieldExpression> childFieldSelections;
    final Orm orm;

    public QueryChildFindOneStateHandlerBuilderImpl(String parentAlias, String parentEntity, String childField, Collection<FieldExpression> childFieldSelections, Orm orm) {
        Objects.requireNonNull(parentAlias);
        Objects.requireNonNull(parentEntity);
        Objects.requireNonNull(childField);
        Objects.requireNonNull(childFieldSelections);
        Objects.requireNonNull(orm);
        this.parentAlias = parentAlias;
        this.parentEntity = parentEntity;
        this.childField = childField;
        this.childFieldSelections = childFieldSelections;
        this.orm = orm;
    }

    @Override
    public MsgEnterEventHandlerP<JsonObject, JsonObject> build() {
        return msg -> orm.findOne(parentEntity, parentAlias, msg.body(), childFieldSelections)
            .map(jsonObject -> {

                final Object child = jsonObject.getValue(childField);

                Objects.requireNonNull(child);

                return Flow.trigger(Events.next, msg.withBody(toJsonObject(child)));
            });
    }

    private JsonObject toJsonObject(Object child) {
        if (child instanceof Map || child instanceof JsonObject) {

            return child instanceof JsonObject
                ? (JsonObject) child
                : new JsonObject(castToMap(child));

        } else if (child instanceof List || child instanceof JsonArray) {

            final JsonArray jsonArray = child instanceof JsonArray
                ? (JsonArray) child
                : new JsonArray(castToList(child));

            if (jsonArray.size() <= 0) {
                throw new QueryChildFindOneStateHandlerException("No child found");
            }

            return jsonArray.getJsonObject(0);

        } else {
            throw new QueryChildFindOneStateHandlerException("Child '" + child + "' is not valid, expected JsonObject or JsonArray");
        }
    }

    private List castToList(Object child) {
        return ((List) child);
    }

    private Map<String, Object> castToMap(Object child) {
        return (Map<String, Object>) child;
    }
}
