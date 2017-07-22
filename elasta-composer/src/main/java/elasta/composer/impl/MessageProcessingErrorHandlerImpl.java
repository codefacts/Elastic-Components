package elasta.composer.impl;

import elasta.composer.MessageProcessingErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * Created by sohan on 7/1/2017.
 */
final public class MessageProcessingErrorHandlerImpl implements MessageProcessingErrorHandler {
    private static final Logger LOGGER = LogManager.getLogger(MessageProcessingErrorHandlerImpl.class);
    final int failureCode;
    final String statusCode;

    public MessageProcessingErrorHandlerImpl(int failureCode, String statusCode) {
        this.failureCode = failureCode;
        Objects.requireNonNull(statusCode);
        this.statusCode = statusCode;
    }

    @Override
    public void onError(Params params) {
        params.getEx().printStackTrace();
        params.getMessage().fail(failureCode, statusCode);
        LOGGER.error("Error processing request", params.getEx());
    }

    public static void main(String[] asf) {

    }
}
