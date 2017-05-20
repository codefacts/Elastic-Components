package elasta.composer.converter;

import elasta.composer.message.handlers.MessageHandler;
import elasta.core.flow.Flow;

/**
 * Created by sohan on 5/20/2017.
 */
public interface FlowToMessageHandlerConverter<T> extends Converter<Flow, MessageHandler<T>> {
    @Override
    MessageHandler<T> convert(Flow flow);
}
