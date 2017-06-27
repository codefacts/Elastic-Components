package elasta.composer.message.handlers.builder;

import elasta.composer.message.handlers.JsonObjectMessageHandler;
import elasta.composer.message.handlers.JsonObjectMessageHandlerBuilder;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 5/14/2017.
 */
public interface AddMessageHandlerBuilder extends JsonObjectMessageHandlerBuilder {
    @Override
    JsonObjectMessageHandler build();
}
