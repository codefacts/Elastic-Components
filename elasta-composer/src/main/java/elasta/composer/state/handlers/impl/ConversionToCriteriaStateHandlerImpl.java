package elasta.composer.state.handlers.impl;

import com.google.common.collect.ImmutableList;
import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.state.handlers.ConversionToCriteriaStateHandler;
import elasta.composer.state.handlers.builder.ex.ConversionToCriteriaStateHandlerException;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.sql.SqlOps;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 6/30/2017.
 */
final public class ConversionToCriteriaStateHandlerImpl implements ConversionToCriteriaStateHandler<JsonObject, JsonObject> {
    final String alias;

    public ConversionToCriteriaStateHandlerImpl(String alias) {
        Objects.requireNonNull(alias);
        this.alias = alias;
    }

    @Override
    public Promise<StateTrigger<Msg<JsonObject>>> handle(Msg<JsonObject> msg) throws Throwable {
        final JsonObject criteria = toCriteria(msg.body());

        return Promises.of(
            Flow.trigger(Events.next, msg.withBody(
                criteria
            ))
        );
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
