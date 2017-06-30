package elasta.composer.state.handlers.builder.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.builder.GenerateIdsAllStateHandlerBuilder;
import elasta.composer.state.handlers.impl.GenerateIdsAllStateHandlerImpl;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.idgenerator.ObjectIdGenerator;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 6/28/2017.
 */
final public class GenerateIdsAllStateHandlerBuilderImpl implements GenerateIdsAllStateHandlerBuilder {
    final String entity;
    final ObjectIdGenerator<Object> objectIdGenerator;

    public GenerateIdsAllStateHandlerBuilderImpl(String entity, ObjectIdGenerator<Object> objectIdGenerator) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(objectIdGenerator);
        this.entity = entity;
        this.objectIdGenerator = objectIdGenerator;
    }

    @Override
    public MsgEnterEventHandlerP<JsonArray, JsonArray> build() {
        return new GenerateIdsAllStateHandlerImpl(
            entity, objectIdGenerator
        );
    }
}
