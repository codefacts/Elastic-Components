package elasta.core.flow;

import elasta.core.promise.impl.Promises;

/**
 * Created by Khan on 5/7/2016.
 */
public class StateTransitionHandlers<T, R> {
    final EnterEventHandlerP<T, R> onEnter;
    final ExitEventHandlerP onExit;

    public StateTransitionHandlers(EnterEventHandlerP<T, R> onEnter, ExitEventHandlerP onExit) {
        this.onEnter = onEnter == null ? defE() : onEnter;
        this.onExit = onExit == null ? defX() : onExit;
    }

    private ExitEventHandlerP defX() {
        return () -> null;
    }

    private static <T, R> EnterEventHandlerP<T, R> defE() {
        return t -> Promises.just(null);
    }

    public EnterEventHandlerP<T, R> getOnEnter() {
        return onEnter;
    }

    public ExitEventHandlerP getOnExit() {
        return onExit;
    }
}
