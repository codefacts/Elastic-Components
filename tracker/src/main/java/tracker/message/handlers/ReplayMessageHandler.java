package tracker.message.handlers;

import elasta.composer.message.handlers.MessageHandler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 2017-07-26.
 */
public interface ReplayMessageHandler extends MessageHandler<JsonObject> {
    @Override
    void handle(Message<JsonObject> event);
}
