package elasta.composer.impl;

import elasta.composer.MessageProcessingErrorHandler;
import io.vertx.core.eventbus.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * Created by sohan on 7/1/2017.
 */
final public class MessageProcessingErrorHandlerImpl implements MessageProcessingErrorHandler {
    private static final Logger log = LogManager.getLogger(MessageProcessingErrorHandlerImpl.class);
    final int failureCode;
    final String statusCode;

    public MessageProcessingErrorHandlerImpl(int failureCode, String statusCode) {
        this.failureCode = failureCode;
        Objects.requireNonNull(statusCode);
        this.statusCode = statusCode;
    }

    @Override
    public void handleError(Throwable throwable, Message message) {
        log.error("Error processing request", throwable);
        message.fail(failureCode, statusCode);
    }

    public static void main(String[] asf) {

    }
}
