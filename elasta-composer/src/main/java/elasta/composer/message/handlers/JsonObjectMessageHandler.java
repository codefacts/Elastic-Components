package elasta.composer.message.handlers;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 5/14/2017.
 */
public interface JsonObjectMessageHandler extends MessageHandler<JsonObject> {
    @Override
    void handle(Message<JsonObject> event);
}
