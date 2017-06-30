package elasta.composer.message.handlers.builder.impl;

import elasta.composer.converter.FlowToJsonArrayMessageHandlerConverter;
import elasta.composer.flow.holder.DeleteAllFlowHolder;
import elasta.composer.message.handlers.JsonArrayMessageHandler;
import elasta.composer.message.handlers.builder.DeleteAllMessageHandlerBuilder;

import java.util.Objects;

/**
 * Created by sohan on 5/21/2017.
 */
final public class DeleteAllMessageHandlerBuilderImpl implements DeleteAllMessageHandlerBuilder {
    final DeleteAllFlowHolder deleteAllFlowHolder;
    final FlowToJsonArrayMessageHandlerConverter flowToJsonArrayMessageHandlerConverter;

    public DeleteAllMessageHandlerBuilderImpl(DeleteAllFlowHolder deleteAllFlowHolder, FlowToJsonArrayMessageHandlerConverter flowToJsonArrayMessageHandlerConverter) {
        Objects.requireNonNull(deleteAllFlowHolder);
        Objects.requireNonNull(flowToJsonArrayMessageHandlerConverter);
        this.deleteAllFlowHolder = deleteAllFlowHolder;
        this.flowToJsonArrayMessageHandlerConverter = flowToJsonArrayMessageHandlerConverter;
    }

    @Override
    public JsonArrayMessageHandler build() {

        return flowToJsonArrayMessageHandlerConverter.convert(deleteAllFlowHolder.getFlow());
    }
}
