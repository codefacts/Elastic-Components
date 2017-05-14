package elasta.composer.message.handlers.builder;

import elasta.composer.message.handlers.MessageHandler;

/**
 * Created by sohan on 5/14/2017.
 */
public interface DeleteMessageHandlerBuilder<T> {
    MessageHandler<T> build();
}
