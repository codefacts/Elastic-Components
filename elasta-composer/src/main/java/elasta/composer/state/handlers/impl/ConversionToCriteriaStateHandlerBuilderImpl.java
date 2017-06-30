package elasta.composer.state.handlers.impl;

import com.google.common.collect.ImmutableList;
import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.ConversionToCriteriaStateHandlerBuilder;
import elasta.composer.state.handlers.ex.ConversionToCriteriaStateHandlerException;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;
import elasta.sql.SqlOps;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class ConversionToCriteriaStateHandlerBuilderImpl implements ConversionToCriteriaStateHandlerBuilder {
    final String alias;

    public ConversionToCriteriaStateHandlerBuilderImpl(String alias) {
        Objects.requireNonNull(alias);
        this.alias = alias;
    }

    @Override
    public MsgEnterEventHandlerP<JsonObject, JsonObject> build() {
        return msg -> {

            final JsonObject criteria = toCriteria(msg.body());

            return Promises.of(
                Flow.trigger(Events.next, msg.withBody(
                    criteria
                ))
            );
        };
    }

    private JsonObject toCriteria(JsonObject criteria) {

        ImmutableList.Builder<JsonObject> criteriaListBuilder = ImmutableList.builder();

        criteria.getMap().forEach((fieldName, value) -> {
            criteriaListBuilder.add(
                SqlOps.eq(alias + "." + fieldName, value)
            );
        });

        List<JsonObject> jsonObjects = criteriaListBuilder.build();

        if (jsonObjects.isEmpty()) {
            throw new ConversionToCriteriaStateHandlerException("No criteria was provided for find one request");
        }

        if (jsonObjects.size() == 1) {
            return jsonObjects.get(0);
        }

        return SqlOps.and(jsonObjects);
    }
}
