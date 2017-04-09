package elasta.orm.event.builder.impl;

import elasta.core.promise.impl.Promises;
import elasta.orm.event.EventHandler;
import elasta.orm.event.builder.EntityToEventHandlerMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 3/30/2017.
 */
final public class EntityToEventHandlerMapImpl implements EntityToEventHandlerMap {
    final EventHandler noOpsEventHandler = params -> Promises.of(params.getEntity());
    final Map<String, List<EventHandler>> entityToEventHandlersMap;

    public EntityToEventHandlerMapImpl(Map<String, List<EventHandler>> entityToEventHandlersMap) {
        Objects.requireNonNull(entityToEventHandlersMap);
        this.entityToEventHandlersMap = entityToEventHandlersMap;
    }

    @Override
    public EventHandler get(String entity) {
        return new DelegatingEventHandlerImpl(entityToEventHandlersMap.getOrDefault(entity, Collections.emptyList()));
    }
}
