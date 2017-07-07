package elasta.composer.impl;

import elasta.composer.MessageProcessingErrorHandler;

import java.util.Objects;

/**
 * Created by sohan on 7/1/2017.
 */
final public class MessageProcessingErrorHandlerImpl implements MessageProcessingErrorHandler {
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
    }

    public static void main(String[] asf) {

    }
}
