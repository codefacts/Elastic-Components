package elasta.composer.message.handlers.builder.impl;

import elasta.composer.converter.FlowToJsonObjectMessageHandlerConverter;
import elasta.composer.flow.holder.FindAllFlowHolder;
import elasta.composer.message.handlers.JsonObjectMessageHandler;
import elasta.composer.message.handlers.builder.FindAllMessageHandlerBuilder;

import java.util.Objects;

/**
 * Created by sohan on 5/14/2017.
 */
final public class FindAllMessageHandlerBuilderImpl implements FindAllMessageHandlerBuilder {
    final FindAllFlowHolder findAllFlowHolder;
    final FlowToJsonObjectMessageHandlerConverter flowToJsonObjectMessageHandlerConverter;

    public FindAllMessageHandlerBuilderImpl(FindAllFlowHolder findAllFlowHolder, FlowToJsonObjectMessageHandlerConverter flowToJsonObjectMessageHandlerConverter) {
        Objects.requireNonNull(findAllFlowHolder);
        Objects.requireNonNull(flowToJsonObjectMessageHandlerConverter);
        this.findAllFlowHolder = findAllFlowHolder;
        this.flowToJsonObjectMessageHandlerConverter = flowToJsonObjectMessageHandlerConverter;
    }

    @Override
    public JsonObjectMessageHandler build() {

        return flowToJsonObjectMessageHandlerConverter.convert(findAllFlowHolder.getFlow());
    }
}
