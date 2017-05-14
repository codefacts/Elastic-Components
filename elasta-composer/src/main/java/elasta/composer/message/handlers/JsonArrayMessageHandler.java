package elasta.composer.message.handlers;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;

/**
 * Created by sohan on 5/14/2017.
 */
public interface JsonArrayMessageHandler extends MessageHandler<JsonArray> {
    @Override
    void handle(Message<JsonArray> event);
}
