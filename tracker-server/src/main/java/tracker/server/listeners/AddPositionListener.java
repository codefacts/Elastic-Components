package tracker.server.listeners;

import elasta.composer.message.handlers.JsonObjectMessageHandler;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 7/11/2017.
 */
public interface AddPositionListener extends JsonObjectMessageHandler {
    @Override
    void handle(Message<JsonObject> event);
}
