package elasta.core.flow;

import elasta.core.intfs.Fun1Unckd;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;

import java.util.concurrent.Callable;

/**
 * Created by Khan on 5/7/2016.
 */
public class StateTransitionHandlers<T, R> {
    final EnterEventHandler<T, R> onEnter;
    final ExitEventHandler onExit;

    public StateTransitionHandlers(EnterEventHandler<T, R> onEnter, ExitEventHandler onExit) {
        this.onEnter = onEnter == null ? defE() : onEnter;
        this.onExit = onExit == null ? defX() : onExit;
    }

    private ExitEventHandler defX() {
        return () -> null;
    }

    private static <T, R> EnterEventHandler<T, R> defE() {
        return t -> Promises.just(null);
    }

    public EnterEventHandler<T, R> getOnEnter() {
        return onEnter;
    }

    public ExitEventHandler getOnExit() {
        return onExit;
    }
}
