package elasta.core.flow;

import elasta.core.intfs.Fun1Unckd;

/**
 * Created by Jango on 11/2/2016.
 */
public class ErrorHandlerAndStatePair<R> {
    private final Fun1Unckd<? extends Throwable, R> errorHandler;
    private final String nextState;

    public ErrorHandlerAndStatePair(Fun1Unckd<? extends Throwable, R> errorHandler, String nextState) {
        this.errorHandler = errorHandler;
        this.nextState = nextState;
    }

    public Fun1Unckd<? extends Throwable, R> getErrorHandler() {
        return errorHandler;
    }

    public String getNextState() {
        return nextState;
    }
}
