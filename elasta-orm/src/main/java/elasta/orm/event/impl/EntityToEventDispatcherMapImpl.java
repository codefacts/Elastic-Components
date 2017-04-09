package elasta.orm.event.impl;

import elasta.orm.event.EntityToEventDispatcherMap;
import elasta.orm.event.EventDispatcher;
import elasta.orm.event.OperationType;
import elasta.orm.event.ex.EntityToEventDispatcherMapException;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 3/30/2017.
 */
final public class EntityToEventDispatcherMapImpl implements EntityToEventDispatcherMap {
    final Map<String, EventHandlerTpl> handlerMap;

    public EntityToEventDispatcherMapImpl(Map<String, EventHandlerTpl> handlerMap) {
        Objects.requireNonNull(handlerMap);
        this.handlerMap = handlerMap;
    }

    @Override
    public Optional<EventDispatcher> getEventDispatcher(String entityName, OperationType operationType) {
        EventHandlerTpl handlerTpl = handlerMap.get(entityName);
        if (handlerTpl == null) {
            return Optional.empty();
        }
        switch (operationType) {
            case UPSERT:
                return handlerTpl.getUpsertEventDispatcher();
            case DELETE:
                return handlerTpl.getDeleteEventDispatcher();
        }
        throw new EntityToEventDispatcherMapException("Invalid operation type '" + operationType + "'");
    }

    public static class EventHandlerTpl {
        final EventDispatcher deleteEventDispatcher;
        final EventDispatcher upsertEventDispatcher;

        public EventHandlerTpl(EventDispatcher deleteEventDispatcher, EventDispatcher upsertEventDispatcher) {
            this.deleteEventDispatcher = deleteEventDispatcher;
            this.upsertEventDispatcher = upsertEventDispatcher;
        }

        public Optional<EventDispatcher> getDeleteEventDispatcher() {
            return Optional.ofNullable(deleteEventDispatcher);
        }

        public Optional<EventDispatcher> getUpsertEventDispatcher() {
            return Optional.ofNullable(upsertEventDispatcher);
        }
    }
}
