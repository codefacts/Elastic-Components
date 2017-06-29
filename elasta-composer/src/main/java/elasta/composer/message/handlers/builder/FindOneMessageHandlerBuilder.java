package elasta.composer.message.handlers.builder;

import elasta.composer.message.handlers.MessageHandler;
import elasta.composer.message.handlers.MessageHandlerBuilder;
import elasta.core.flow.Flow;

/**
 * Created by sohan on 5/14/2017.
 */
public interface FindOneMessageHandlerBuilder<T> extends MessageHandlerBuilder<T> {
    MessageHandler<T> build();
}
