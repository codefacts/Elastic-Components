package elasta.core.flow;

import elasta.core.intfs.FunctionUnchecked;

/**
 * Created by Jango on 10/31/2016.
 */
public class ErrorToStateMapping<R> {
    private final Class<? extends Throwable> errorClass;
    private final String nextState;
    private final FunctionUnchecked<? extends Throwable, R> errorHandler;

    public ErrorToStateMapping(Class<? extends Throwable> errorClass, String nextState, FunctionUnchecked<? extends Throwable, R> errorHandler) {
        this.errorClass = errorClass;
        this.nextState = nextState;
        this.errorHandler = errorHandler;
    }

    public Class<? extends Throwable> getErrorClass() {
        return errorClass;
    }

    public String getNextState() {
        return nextState;
    }

    public FunctionUnchecked<? extends Throwable, R> getErrorHandler() {
        return errorHandler;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ErrorToStateMapping that = (ErrorToStateMapping) o;

        if (errorClass != null ? !errorClass.equals(that.errorClass) : that.errorClass != null) return false;
        return nextState != null ? nextState.equals(that.nextState) : that.nextState == null;

    }

    @Override
    public int hashCode() {
        int result = errorClass != null ? errorClass.hashCode() : 0;
        result = 31 * result + (nextState != null ? nextState.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ErrorToStateMapping{" +
            "errorClass=" + errorClass +
            ", nextState='" + nextState + '\'' +
            '}';
    }
}
