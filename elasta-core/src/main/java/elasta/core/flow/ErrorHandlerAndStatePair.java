package elasta.core.flow;

import elasta.core.intfs.FunctionUnchecked;

/**
 * Created by Jango on 11/2/2016.
 */
public class ErrorHandlerAndStatePair<R> {
    private final FunctionUnchecked<? extends Throwable, R> errorHandler;
    private final String nextState;

    public ErrorHandlerAndStatePair(FunctionUnchecked<? extends Throwable, R> errorHandler, String nextState) {
        this.errorHandler = errorHandler;
        this.nextState = nextState;
    }

    public FunctionUnchecked<? extends Throwable, R> getErrorHandler() {
        return errorHandler;
    }

    public String getNextState() {
        return nextState;
    }
}
