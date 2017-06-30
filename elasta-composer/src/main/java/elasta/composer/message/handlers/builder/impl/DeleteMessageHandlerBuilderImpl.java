package elasta.composer.message.handlers.builder.impl;

import elasta.composer.converter.FlowToMessageHandlerConverter;
import elasta.composer.flow.holder.DeleteFlowHolder;
import elasta.composer.message.handlers.MessageHandler;
import elasta.composer.message.handlers.builder.DeleteMessageHandlerBuilder;

import java.util.Objects;

/**
 * Created by sohan on 5/14/2017.
 */
final public class DeleteMessageHandlerBuilderImpl<T> implements DeleteMessageHandlerBuilder<T> {
    final DeleteFlowHolder deleteFlowHolder;
    final FlowToMessageHandlerConverter<T> flowToMessageHandlerConverter;

    public DeleteMessageHandlerBuilderImpl(DeleteFlowHolder deleteFlowHolder, FlowToMessageHandlerConverter<T> flowToMessageHandlerConverter) {
        Objects.requireNonNull(deleteFlowHolder);
        Objects.requireNonNull(flowToMessageHandlerConverter);
        this.deleteFlowHolder = deleteFlowHolder;
        this.flowToMessageHandlerConverter = flowToMessageHandlerConverter;
    }

    @Override
    public MessageHandler<T> build() {

        return flowToMessageHandlerConverter.convert(deleteFlowHolder.getFlow());
    }
}
