package elasta.orm.event;

import io.vertx.core.json.JsonObject;

import java.util.Optional;

/**
 * Created by sohan on 3/28/2017.
 */
public interface EntityToEventDispatcherMap {

    Optional<EventDispatcher> getEventDispatcher(String entityName, OperationType operationType);
}
