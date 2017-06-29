package elasta.composer.message.handlers;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 6/29/2017.
 */
public interface JsonArrayMessageHandlerBuilder extends MessageHandlerBuilder<JsonArray> {
    JsonArrayMessageHandler build();
}
