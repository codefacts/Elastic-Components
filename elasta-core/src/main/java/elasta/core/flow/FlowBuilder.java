package elasta.core.flow;

import elasta.core.flow.impl.FlowBuilderImpl;
import elasta.core.intfs.Fun1Unckd;
import elasta.core.promise.intfs.Promise;

import java.util.concurrent.Callable;

public interface FlowBuilder {

    public FlowBuilder initialState(String initialState);

    public FlowBuilder when(String state, EventAndState... stateEntries);

    public FlowBuilder exec(String state, StateTransitionHandlers stateTransitionHandlers);

    public <T, R> FlowBuilder handlers(String state, EnterEventHandler<T, R> onEnter);

    public <T, R> FlowBuilder handlers(String state, EnterEventHandler<T, R> onEnter, ExitEventHandler onExit);

    public Flow build();

    public static FlowBuilder create() {
        return new FlowBuilderImpl();
    }
}