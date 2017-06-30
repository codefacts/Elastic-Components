package elasta.composer.message.handlers.builder.impl;

import elasta.composer.converter.FlowToJsonArrayMessageHandlerConverter;
import elasta.composer.flow.holder.AddAllFlowHolder;
import elasta.composer.message.handlers.JsonArrayMessageHandler;
import elasta.composer.message.handlers.builder.AddAllMessageHandlerBuilder;

import java.util.Objects;

/**
 * Created by sohan on 5/21/2017.
 */
final public class AddAllMessageHandlerBuilderImpl implements AddAllMessageHandlerBuilder {
    final AddAllFlowHolder addAllFlowHolder;
    final FlowToJsonArrayMessageHandlerConverter flowToJsonArrayMessageHandlerConverter;

    public AddAllMessageHandlerBuilderImpl(AddAllFlowHolder addAllFlowHolder, FlowToJsonArrayMessageHandlerConverter flowToJsonArrayMessageHandlerConverter) {
        Objects.requireNonNull(addAllFlowHolder);
        Objects.requireNonNull(flowToJsonArrayMessageHandlerConverter);
        this.addAllFlowHolder = addAllFlowHolder;
        this.flowToJsonArrayMessageHandlerConverter = flowToJsonArrayMessageHandlerConverter;
    }

    @Override
    public JsonArrayMessageHandler build() {

        return flowToJsonArrayMessageHandlerConverter.convert(addAllFlowHolder.getFlow());
    }
}
