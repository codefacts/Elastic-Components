package elasta.core.flow;

public class ErrorToStateMappingBuilder {
    private Class<? extends Throwable> errorClass;
    private String nextState;

    public ErrorToStateMappingBuilder setErrorClass(Class<? extends Throwable> errorClass) {
        this.errorClass = errorClass;
        return this;
    }

    public ErrorToStateMappingBuilder setNextState(String nextState) {
        this.nextState = nextState;
        return this;
    }

    public ErrorToStateMapping createErrorToStateMapping() {
        return new ErrorToStateMapping(errorClass, nextState);
    }
}