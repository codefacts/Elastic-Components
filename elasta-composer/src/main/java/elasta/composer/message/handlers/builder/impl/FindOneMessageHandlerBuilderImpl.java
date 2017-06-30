package elasta.composer.message.handlers.builder.impl;

import elasta.composer.converter.FlowToJsonObjectMessageHandlerConverter;
import elasta.composer.flow.holder.FindOneFlowHolder;
import elasta.composer.message.handlers.JsonObjectMessageHandler;
import elasta.composer.message.handlers.builder.FindOneMessageHandlerBuilder;

import java.util.Objects;

/**
 * Created by sohan on 5/14/2017.
 */
final public class FindOneMessageHandlerBuilderImpl<T> implements FindOneMessageHandlerBuilder {
    final FindOneFlowHolder findOneFlowHolder;
    final FlowToJsonObjectMessageHandlerConverter flowToMessageHandlerConverter;

    public FindOneMessageHandlerBuilderImpl(FindOneFlowHolder findOneFlowHolder, FlowToJsonObjectMessageHandlerConverter flowToMessageHandlerConverter) {
        Objects.requireNonNull(findOneFlowHolder);
        Objects.requireNonNull(flowToMessageHandlerConverter);
        this.findOneFlowHolder = findOneFlowHolder;
        this.flowToMessageHandlerConverter = flowToMessageHandlerConverter;
    }

    @Override
    public JsonObjectMessageHandler build() {

        return flowToMessageHandlerConverter.convert(findOneFlowHolder.getFlow());
    }
}
