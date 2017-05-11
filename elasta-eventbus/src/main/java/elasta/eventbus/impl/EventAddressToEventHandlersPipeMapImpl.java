package elasta.eventbus.impl;

import elasta.eventbus.EventAddressToEventHandlersPipeMap;
import elasta.eventbus.EventHandlersPipe;

import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 5/9/2017.
 */
final public class EventAddressToEventHandlersPipeMapImpl implements EventAddressToEventHandlersPipeMap {
    final Map<String, EventHandlersPipe> eventHandlersPipeMap;

    public EventAddressToEventHandlersPipeMapImpl(Map<String, EventHandlersPipe> eventHandlersPipeMap) {
        Objects.requireNonNull(eventHandlersPipeMap);
        this.eventHandlersPipeMap = eventHandlersPipeMap;
    }

    @Override
    public Map<String, EventHandlersPipe> getMap() {
        return eventHandlersPipeMap;
    }
}
