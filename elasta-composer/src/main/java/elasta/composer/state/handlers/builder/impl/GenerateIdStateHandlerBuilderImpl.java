package elasta.composer.state.handlers.builder.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.builder.GenerateIdStateHandlerBuilder;
import elasta.composer.state.handlers.impl.GenerateIdStateHandlerImpl;
import elasta.core.flow.Flow;
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

        return new GenerateIdStateHandlerImpl(
            entity, objectIdGenerator
        );
    }
}
