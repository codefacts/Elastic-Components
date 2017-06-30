package elasta.composer.message.handlers.builder.impl;

import elasta.composer.converter.FlowToJsonObjectMessageHandlerConverter;
import elasta.composer.flow.holder.UpdateFlowHolder;
import elasta.composer.message.handlers.JsonObjectMessageHandler;
import elasta.composer.message.handlers.builder.UpdateMessageHandlerBuilder;

import java.util.Objects;

/**
 * Created by sohan on 5/14/2017.
 */
final public class UpdateMessageHandlerBuilderImpl implements UpdateMessageHandlerBuilder {
    final UpdateFlowHolder updateFlowHolder;
    final FlowToJsonObjectMessageHandlerConverter flowToJsonObjectMessageHandlerConverter;

    public UpdateMessageHandlerBuilderImpl(UpdateFlowHolder updateFlowHolder, FlowToJsonObjectMessageHandlerConverter flowToJsonObjectMessageHandlerConverter) {
        Objects.requireNonNull(updateFlowHolder);
        Objects.requireNonNull(flowToJsonObjectMessageHandlerConverter);
        this.updateFlowHolder = updateFlowHolder;
        this.flowToJsonObjectMessageHandlerConverter = flowToJsonObjectMessageHandlerConverter;
    }

    @Override
    public JsonObjectMessageHandler build() {

        return flowToJsonObjectMessageHandlerConverter.convert(updateFlowHolder.getFlow());
    }
}
