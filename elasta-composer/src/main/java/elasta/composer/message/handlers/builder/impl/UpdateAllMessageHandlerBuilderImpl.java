package elasta.composer.message.handlers.builder.impl;

import elasta.composer.converter.FlowToJsonArrayMessageHandlerConverter;
import elasta.composer.flow.holder.UpdateAllFlowHolder;
import elasta.composer.message.handlers.JsonArrayMessageHandler;
import elasta.composer.message.handlers.builder.UpdateAllMessageHandlerBuilder;

import java.util.Objects;

/**
 * Created by sohan on 5/21/2017.
 */
final public class UpdateAllMessageHandlerBuilderImpl implements UpdateAllMessageHandlerBuilder {
    final UpdateAllFlowHolder updateAllFlowHolder;
    final FlowToJsonArrayMessageHandlerConverter flowToJsonArrayMessageHandlerConverter;

    public UpdateAllMessageHandlerBuilderImpl(UpdateAllFlowHolder updateAllFlowHolder, FlowToJsonArrayMessageHandlerConverter flowToJsonArrayMessageHandlerConverter) {
        Objects.requireNonNull(updateAllFlowHolder);
        Objects.requireNonNull(flowToJsonArrayMessageHandlerConverter);
        this.updateAllFlowHolder = updateAllFlowHolder;
        this.flowToJsonArrayMessageHandlerConverter = flowToJsonArrayMessageHandlerConverter;
    }

    @Override
    public JsonArrayMessageHandler build() {

        return flowToJsonArrayMessageHandlerConverter.convert(updateAllFlowHolder.getFlow());
    }
}
