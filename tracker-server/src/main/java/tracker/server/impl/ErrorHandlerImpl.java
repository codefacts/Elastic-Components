package tracker.server.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tracker.server.ErrorHandler;

/**
 * Created by sohan on 2017-07-23.
 */
final public class ErrorHandlerImpl implements ErrorHandler {
    public static final Logger log = LogManager.getLogger(ErrorHandlerImpl.class);

    @Override
    public void handleError(Throwable throwable) {
        log.error("Unexpected error occurred", throwable);
    }
}
