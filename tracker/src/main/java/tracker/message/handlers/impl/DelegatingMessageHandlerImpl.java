package tracker.message.handlers.impl;

import elasta.composer.MessageProcessingErrorHandler;
import elasta.composer.message.handlers.MessageHandler;
import io.vertx.core.eventbus.Message;

import java.util.Objects;

/**
 * Created by sohan on 7/1/2017.
 */
final public class DelegatingMessageHandlerImpl implements MessageHandler<Object> {
    final MessageHandler messageHandler;
    final MessageProcessingErrorHandler messageProcessingErrorHandler;

    public DelegatingMessageHandlerImpl(MessageHandler messageHandler, MessageProcessingErrorHandler messageProcessingErrorHandler) {
        Objects.requireNonNull(messageHandler);
        Objects.requireNonNull(messageProcessingErrorHandler);
        this.messageHandler = messageHandler;
        this.messageProcessingErrorHandler = messageProcessingErrorHandler;
    }

    @Override
    public void handle(Message<Object> message) {
        try {

            messageHandler.handle(message);

        } catch (Exception ex) {
            messageProcessingErrorHandler.onError(
                MessageProcessingErrorHandler.Params.builder()
                    .ex(ex)
                    .message(message)
                    .build()
            );
        }
    }
}
