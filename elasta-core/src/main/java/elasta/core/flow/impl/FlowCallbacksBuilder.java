package elasta.core.flow.impl;

import elasta.core.flow.EnterEventHandler;
import elasta.core.flow.ExitEventHandler;
import elasta.core.flow.StateTransitionHandlers;
import elasta.core.flow.StateTrigger;
import elasta.core.intfs.Fun1Unckd;
import elasta.core.promise.intfs.Promise;

import java.util.concurrent.Callable;

public class FlowCallbacksBuilder<T, R> {
    private EnterEventHandler<T, R> onEnter;
    private ExitEventHandler onExit;

    public FlowCallbacksBuilder() {
    }

    public FlowCallbacksBuilder<T, R> onEnter(EnterEventHandler<T, R> onEnter) {
        this.onEnter = onEnter;
        return this;
    }

    public FlowCallbacksBuilder<T, R> onExit(ExitEventHandler onExit) {
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