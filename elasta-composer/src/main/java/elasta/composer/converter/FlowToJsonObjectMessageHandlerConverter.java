package elasta.composer.converter;

import elasta.composer.message.handlers.JsonObjectMessageHandler;
import elasta.composer.message.handlers.MessageHandler;
import elasta.core.flow.Flow;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 5/20/2017.
 */
public interface FlowToJsonObjectMessageHandlerConverter extends FlowToMessageHandlerConverter<JsonObject> {
    @Override
    JsonObjectMessageHandler convert(Flow flow);
}
