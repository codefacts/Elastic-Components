package elasta.core.flow;

import elasta.core.intfs.Fun1Unckd;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;

import java.util.concurrent.Callable;

/**
 * Created by Khan on 5/7/2016.
 */
public class StateTransitionHandlers<T, R> {
    final Fun1Unckd<T, Promise<StateTrigger<R>>> onEnter;
    final Callable<Promise<Void>> onExit;

    public StateTransitionHandlers(Fun1Unckd<T, Promise<StateTrigger<R>>> onEnter, Callable<Promise<Void>> onExit) {
        this.onEnter = onEnter == null ? defE() : onEnter;
        this.onExit = onExit == null ? defX() : onExit;
    }

    private Callable<Promise<Void>> defX() {
        return () -> null;
    }

    private static <T, R> Fun1Unckd<T, Promise<StateTrigger<R>>> defE() {
        return t -> Promises.just(null);
    }

    public Fun1Unckd<T, Promise<StateTrigger<R>>> getOnEnter() {
        return onEnter;
    }

    public Callable<Promise<Void>> getOnExit() {
        return onExit;
    }
}
