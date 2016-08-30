package elasta.core.statemachine;

import elasta.core.intfs.FunctionUnchecked;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;

import java.util.concurrent.Callable;

/**
 * Created by Khan on 5/7/2016.
 */
public class StateCallbacks<T, R> {
    final FunctionUnchecked<T, Promise<StateTrigger<R>>> onEnter;
    final Callable<Promise<Void>> onExit;

    protected StateCallbacks(FunctionUnchecked<T, Promise<StateTrigger<R>>> onEnter, Callable<Promise<Void>> onExit) {
        this.onEnter = onEnter == null ? defE() : onEnter;
        this.onExit = onExit == null ? defX() : onExit;
    }

    private Callable<Promise<Void>> defX() {
        return () -> null;
    }

    private static <T, R> FunctionUnchecked<T, Promise<StateTrigger<R>>> defE() {
        return t -> Promises.just(null);
    }
}
