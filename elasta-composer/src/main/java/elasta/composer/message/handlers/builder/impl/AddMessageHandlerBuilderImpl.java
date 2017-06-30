package elasta.composer.message.handlers.builder.impl;

import elasta.composer.converter.FlowToJsonObjectMessageHandlerConverter;
import elasta.composer.flow.holder.AddFlowHolder;
import elasta.composer.message.handlers.JsonObjectMessageHandler;
import elasta.composer.message.handlers.builder.AddMessageHandlerBuilder;

import java.util.Objects;

/**
 * Created by sohan on 5/14/2017.
 */
final public class AddMessageHandlerBuilderImpl implements AddMessageHandlerBuilder {
    final AddFlowHolder addFlowHolder;
    final FlowToJsonObjectMessageHandlerConverter flowToJsonObjectMessageHandlerConverter;

    public AddMessageHandlerBuilderImpl(AddFlowHolder addFlowHolder, FlowToJsonObjectMessageHandlerConverter flowToJsonObjectMessageHandlerConverter) {
        Objects.requireNonNull(addFlowHolder);
        Objects.requireNonNull(flowToJsonObjectMessageHandlerConverter);
        this.addFlowHolder = addFlowHolder;
        this.flowToJsonObjectMessageHandlerConverter = flowToJsonObjectMessageHandlerConverter;
    }

    @Override
    public JsonObjectMessageHandler build() {

        return flowToJsonObjectMessageHandlerConverter.convert(addFlowHolder.getFlow());
    }
}
