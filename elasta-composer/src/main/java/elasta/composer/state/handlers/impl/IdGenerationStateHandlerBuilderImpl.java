package elasta.composer.state.handlers.impl;

import com.google.common.collect.ImmutableMap;
import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.orm.upsert.IdGenerator;
import elasta.composer.state.handlers.IdGenerationStateHandlerBuilder;
import elasta.core.flow.Flow;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class IdGenerationStateHandlerBuilderImpl implements IdGenerationStateHandlerBuilder {
    final String entity;
    final String primaryKey;
    final IdGenerator idGenerator;

    public IdGenerationStateHandlerBuilderImpl(String entity, String primaryKey, IdGenerator idGenerator) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(primaryKey);
        Objects.requireNonNull(idGenerator);
        this.entity = entity;
        this.primaryKey = primaryKey;
        this.idGenerator = idGenerator;
    }

    @Override
    public MsgEnterEventHandlerP<JsonObject, JsonObject> build() {
        return msg -> idGenerator.nextId(entity)
            .map(id -> Flow.trigger(Events.next, msg.withBody(
                new JsonObject(
                    ImmutableMap.<String, Object>builder()
                        .putAll(msg.body().getMap())
                        .put(primaryKey, id)
                        .build()
                )
            )));
    }
}
