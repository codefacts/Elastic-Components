package elasta.composer.impl;

import elasta.composer.MessageProcessingErrorHandler;
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
    public void onError(Params params) {
        Throwable ex = params.getEx();
        ex.printStackTrace();
        log.error("Error processing request", ex);
        params.getMessage().fail(failureCode, statusCode);
    }

    public static void main(String[] asf) {

    }
}
