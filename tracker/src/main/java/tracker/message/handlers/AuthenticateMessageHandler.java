package tracker.message.handlers;

import elasta.composer.message.handlers.JsonObjectMessageHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 7/1/2017.
 */
public interface AuthenticateMessageHandler extends JsonObjectMessageHandler {
    @Override
    void handle(Message<JsonObject> event);
}
