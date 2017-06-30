package elasta.composer.converter.impl;

import elasta.composer.ConvertersMap;
import elasta.composer.MessageProcessingErrorHandler;
import elasta.composer.converter.FlowToJsonArrayMessageHandlerConverter;
import elasta.composer.converter.FlowToMessageHandlerConverter;
import elasta.composer.message.handlers.JsonArrayMessageHandler;
import elasta.composer.converter.UserIdConverter;
import elasta.core.flow.Flow;
import io.vertx.core.json.JsonArray;

/**
 * Created by sohan on 6/29/2017.
 */
final public class FlowToJsonArrayMessageHandlerConverterImpl implements FlowToJsonArrayMessageHandlerConverter {
    final FlowToMessageHandlerConverter<JsonArray> flowToMessageHandlerConverter;

    public FlowToJsonArrayMessageHandlerConverterImpl(MessageProcessingErrorHandler messageProcessingErrorHandler, UserIdConverter userIdConverter, ConvertersMap convertersMap) {
        this.flowToMessageHandlerConverter = new FlowToMessageHandlerConverterImpl<>(
            messageProcessingErrorHandler,
            userIdConverter,
            convertersMap
        );
    }

    @Override
    public JsonArrayMessageHandler convert(Flow flow) {
        return flowToMessageHandlerConverter.convert(flow)::handle;
    }
}
