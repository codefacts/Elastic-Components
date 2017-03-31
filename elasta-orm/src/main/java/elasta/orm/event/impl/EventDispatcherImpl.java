package elasta.orm.event.impl;

import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.event.*;
import elasta.pipeline.jsonwalker.pathspecs.JsonArrayPathSpecImpl;
import elasta.pipeline.jsonwalker.pathspecs.JsonObjectPathSpecImpl;
import io.vertx.core.json.JsonObject;
import lombok.Value;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/28/2017.
 */
final public class EventDispatcherImpl implements EventDispatcher {
    final EventHandler eventHandler;
    final List<ChildObjectEventHandler> childObjectEventHandlers;

    public EventDispatcherImpl(EventHandler eventHandler, List<ChildObjectEventHandler> childObjectEventHandlers) {
        Objects.requireNonNull(eventHandler);
        Objects.requireNonNull(childObjectEventHandlers);
        this.eventHandler = eventHandler;
        this.childObjectEventHandlers = childObjectEventHandlers;
    }

    @Override
    public Promise<JsonObject> dispatch(EventDispatcher.Params params) {
        return eventHandler
            .handle(
                EventHandler.Params.builder()
                    .entity(params.getEntity())
                    .path(params.getPath())
                    .root(params.getRoot())
                    .operationType(OperationType.UPSERT)
                    .build()
            )
            .mapP(jsonObject -> {

                Objects.requireNonNull(jsonObject);

                List<Promise<KeyValue>> promiseList = childObjectEventHandlers.stream()
                    .map(childObjectEventHandler -> new JsonWalker((jo, index) -> {

                        if (index == -1) {

                            return childObjectEventHandler.getEventDispatcher().dispatch(
                                Params.builder()
                                    .entity(jo)
                                    .path(params.getPath().concat(
                                        new JsonObjectPathSpecImpl(
                                            childObjectEventHandler.getField()
                                        )
                                    ))
                                    .root(params.getRoot())
                                    .build()
                            );

                        } else {

                            return childObjectEventHandler.getEventDispatcher().dispatch(
                                Params.builder()
                                    .entity(jo)
                                    .path(params.getPath().concat(
                                        new JsonArrayPathSpecImpl(
                                            childObjectEventHandler.getField(), index
                                        )
                                    ))
                                    .root(params.getRoot())
                                    .build()
                            );

                        }

                    }).handle(
                        jsonObject.getValue(
                            childObjectEventHandler.getField()
                        )
                    ).map(obj -> new KeyValue(childObjectEventHandler.getField(), obj))).collect(Collectors.toList());

                return Promises.when(promiseList)
                    .map(keyValues -> keyValues.stream().collect(Collectors.toMap(KeyValue::getField, KeyValue::getValue)))
                    .map(JsonObject::new);

            })
            ;
    }

    @Value
    private static final class KeyValue {
        final String field;
        final Object value;

        public KeyValue(String field, Object value) {
            this.field = field;
            this.value = value;
        }
    }
}
