package elasta.composer.state.handlers.impl;

import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.producer.IdGenerator;
import elasta.composer.state.handlers.ChildIdGenerationStateHandlerBuilder;
import elasta.core.flow.Flow;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 5/25/2017.
 */
final public class ChildIdGenerationStateHandlerBuilderImpl implements ChildIdGenerationStateHandlerBuilder {
    final String childEntityField;
    final String childEntity;
    final String childEntityPrimaryKey;
    final IdGenerator idGenerator;

    public ChildIdGenerationStateHandlerBuilderImpl(String childEntityField, String childEntity, String childEntityPrimaryKey, IdGenerator idGenerator) {
        this.childEntityField = childEntityField;
        Objects.requireNonNull(childEntity);
        Objects.requireNonNull(childEntityPrimaryKey);
        Objects.requireNonNull(idGenerator);
        this.childEntity = childEntity;
        this.childEntityPrimaryKey = childEntityPrimaryKey;
        this.idGenerator = idGenerator;
    }

    @Override
    public MsgEnterEventHandlerP<JsonObject, JsonObject> build() {
        
        return msg -> idGenerator.nextId(childEntity)
            .map(id -> {

                final ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();

                final JsonObject parent = msg.body();

                final JsonObject child = parent.getJsonObject(childEntityField);

                Objects.requireNonNull(child);

                parent.getMap().entrySet().stream()
                    .filter(entry -> Utils.not(Objects.equals(entry.getKey(), childEntityField)))
                    .forEach(builder::put);

                final JsonObject newChild = new JsonObject(
                    ImmutableMap.<String, Object>builder()
                        .putAll(child.getMap())
                        .put(childEntityPrimaryKey, id)
                        .build()
                );

                return Flow.trigger(Events.next, msg.withBody(
                    new JsonObject(
                        builder
                            .put(childEntityField, newChild)
                            .build()
                    )
                ));
            });
    }
}
