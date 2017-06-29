package elasta.composer.message.handlers;

import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 5/14/2017.
 */
public interface JsonObjectMessageHandlerBuilder extends MessageHandlerBuilder<JsonObject> {
    JsonObjectMessageHandler build();
}
