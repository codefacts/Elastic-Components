package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.state.handlers.GenerateIdStateHandler;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.intfs.Promise;
import elasta.orm.idgenerator.ObjectIdGenerator;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 6/30/2017.
 */
final public class GenerateIdStateHandlerImpl implements GenerateIdStateHandler<JsonObject, JsonObject> {
    final String entity;
    final ObjectIdGenerator<Object> objectIdGenerator;

    public GenerateIdStateHandlerImpl(String entity, ObjectIdGenerator objectIdGenerator) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(objectIdGenerator);
        this.entity = entity;
        this.objectIdGenerator = objectIdGenerator;
    }

    @Override
    public Promise<StateTrigger<Msg<JsonObject>>> handle(Msg<JsonObject> msg) throws Throwable {

        return objectIdGenerator.generateId(entity, msg.body())
            .map(jsonObject -> Flow.trigger(
                Events.next, msg.withBody(jsonObject)
            ));
    }
}
