package elasta.composer.message.handlers.builder;

import elasta.composer.message.handlers.JsonArrayMessageHandler;
import elasta.composer.message.handlers.JsonArrayMessageHandlerBuilder;
import elasta.composer.message.handlers.MessageHandler;
import io.vertx.core.json.JsonArray;

import java.util.List;

/**
 * Created by sohan on 5/21/2017.
 */
public interface DeleteAllMessageHandlerBuilder extends JsonArrayMessageHandlerBuilder {
    @Override
    JsonArrayMessageHandler build();
}
