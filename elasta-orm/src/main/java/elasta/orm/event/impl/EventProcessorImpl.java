package elasta.orm.event.impl;

import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.event.EntityToEventDispatcherMap;
import elasta.orm.event.EventDispatcher;
import elasta.orm.event.EventProcessor;
import elasta.orm.event.OperationType;
import elasta.pipeline.jsonwalker.JsonPath;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 3/28/2017.
 */
final public class EventProcessorImpl implements EventProcessor {
    final EntityToEventDispatcherMap entityToEventDispatcherMap;

    public EventProcessorImpl(EntityToEventDispatcherMap entityToEventDispatcherMap) {
        Objects.requireNonNull(entityToEventDispatcherMap);
        this.entityToEventDispatcherMap = entityToEventDispatcherMap;
    }

    @Override
    public Promise<JsonObject> processDelete(String entityName, JsonObject entity) {
        return entityToEventDispatcherMap.getEventDispatcher(entityName, OperationType.DELETE)
            .map(
                eventDispatcher -> eventDispatcher.dispatch(
                    EventDispatcher.Params.builder()
                        .entity(entity)
                        .path(JsonPath.root())
                        .root(entity)
                        .build()
                )
            ).orElse(Promises.of(entity));
    }

    @Override
    public Promise<JsonObject> processUpsert(String entityName, JsonObject entity) {
        return entityToEventDispatcherMap.getEventDispatcher(entityName, OperationType.UPSERT)
            .map(
                eventDispatcher -> eventDispatcher.dispatch(
                    EventDispatcher.Params.builder()
                        .entity(entity)
                        .path(JsonPath.root())
                        .root(entity)
                        .build()
                )
            ).orElse(Promises.of(entity));
    }
}
