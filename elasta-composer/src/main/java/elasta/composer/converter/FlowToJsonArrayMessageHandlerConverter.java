package elasta.composer.converter;

import elasta.composer.message.handlers.JsonArrayMessageHandler;
import elasta.composer.message.handlers.MessageHandler;
import elasta.core.flow.Flow;
import io.vertx.core.json.JsonArray;

/**
 * Created by sohan on 5/20/2017.
 */
public interface FlowToJsonArrayMessageHandlerConverter extends FlowToMessageHandlerConverter<JsonArray> {
    @Override
    JsonArrayMessageHandler convert(Flow flow);
}
