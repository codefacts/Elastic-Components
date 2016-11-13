package elasta.core.flow.impl;

import elasta.core.flow.EnterEventHandlerP;
import elasta.core.flow.ExitEventHandlerP;
import elasta.core.flow.StateTransitionHandlers;

public class FlowCallbacksBuilder<T, R> {
    private EnterEventHandlerP<T, R> onEnter;
    private ExitEventHandlerP onExit;

    public FlowCallbacksBuilder() {
    }

    public FlowCallbacksBuilder<T, R> onEnter(EnterEventHandlerP<T, R> onEnter) {
        this.onEnter = onEnter;
        return this;
    }

    public FlowCallbacksBuilder<T, R> onExit(ExitEventHandlerP onExit) {
        this.onExit = onExit;
        return this;
    }

    public StateTransitionHandlers<T, R> build() {
        return new StateTransitionHandlers<>(onEnter, onExit);
    }

    public static <T, R> FlowCallbacksBuilder<T, R> create() {
        return new FlowCallbacksBuilder<>();
    }
}