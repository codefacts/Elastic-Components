package elasta.eventbus;

import java.util.Map;

/**
 * Created by sohan on 5/9/2017.
 */
public interface EventAddressToEventHandlersPipeMap {
    Map<String, EventHandlersPipe> getMap();
}
