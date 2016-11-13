package elasta.core.flow;

import elasta.core.flow.impl.FlowBuilderImpl;

public interface FlowBuilder {

    public FlowBuilder initialState(String initialState);

    public FlowBuilder when(String state, EventAndState... stateEntries);

    FlowBuilder replace(String prevState, String newState);

    FlowBuilder remove(String state);

    public FlowBuilder exec(String state, StateTransitionHandlers stateTransitionHandlers);

    public <T, R> FlowBuilder handlers(String state, EnterEventHandler<T, R> onEnter);

    public <T, R> FlowBuilder handlers(String state, EnterEventHandler<T, R> onEnter, ExitEventHandler onExit);

    public <T, R> FlowBuilder handlersP(String state, EnterEventHandlerP<T, R> onEnter);

    public <T, R> FlowBuilder handlersP(String state, EnterEventHandlerP<T, R> onEnter, ExitEventHandlerP onExit);

    public Flow build();

    public static FlowBuilder create() {
        return new FlowBuilderImpl();
    }

    static FlowBuilder create(Flow flow) {
        return new FlowBuilderImpl(flow);
    }
}