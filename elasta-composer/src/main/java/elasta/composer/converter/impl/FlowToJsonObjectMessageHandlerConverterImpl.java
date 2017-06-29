package elasta.composer.converter.impl;

import elasta.composer.ConvertersMap;
import elasta.composer.MessageProcessingErrorHandler;
import elasta.composer.converter.FlowToJsonObjectMessageHandlerConverter;
import elasta.composer.converter.FlowToMessageHandlerConverter;
import elasta.composer.message.handlers.JsonObjectMessageHandler;
import elasta.composer.state.handlers.UserIdConverter;
import elasta.core.flow.Flow;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 6/29/2017.
 */
final public class FlowToJsonObjectMessageHandlerConverterImpl implements FlowToJsonObjectMessageHandlerConverter {
    final FlowToMessageHandlerConverter<JsonObject> flowToMessageHandlerConverter;

    public FlowToJsonObjectMessageHandlerConverterImpl(MessageProcessingErrorHandler messageProcessingErrorHandler, UserIdConverter userIdConverter, ConvertersMap convertersMap) {
        this.flowToMessageHandlerConverter = new FlowToMessageHandlerConverterImpl<>(
            messageProcessingErrorHandler,
            userIdConverter,
            convertersMap
        );
    }

    @Override
    public JsonObjectMessageHandler convert(Flow flow) {

        return flowToMessageHandlerConverter.convert(flow)::handle;
    }
}
