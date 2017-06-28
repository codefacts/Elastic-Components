package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.GenerateIdStateHandlerBuilder;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.intfs.Promise;
import elasta.orm.idgenerator.ObjectIdGenerator;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 6/28/2017.
 */
final public class GenerateIdStateHandlerBuilderImpl implements GenerateIdStateHandlerBuilder {
    final String entity;
    final ObjectIdGenerator<Object> objectIdGenerator;

    public GenerateIdStateHandlerBuilderImpl(String entity, ObjectIdGenerator objectIdGenerator) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(objectIdGenerator);
        this.entity = entity;
        this.objectIdGenerator = objectIdGenerator;
    }

    @Override
    public MsgEnterEventHandlerP<JsonObject, JsonObject> build() {

        return
            msg -> objectIdGenerator.generateId(entity, msg.body())
                .map(jsonObject -> Flow.trigger(
                    Events.next, msg.withBody(jsonObject)
                ));
    }
}
