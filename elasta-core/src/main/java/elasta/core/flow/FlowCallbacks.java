package elasta.core.flow;

import elasta.core.intfs.Fun1Unckd;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;

import java.util.concurrent.Callable;

/**
 * Created by Khan on 5/7/2016.
 */
public class FlowCallbacks<T, R> {
    final Fun1Unckd<T, Promise<FlowTrigger<R>>> onEnter;
    final Callable<Promise<Void>> onExit;

    protected FlowCallbacks(Fun1Unckd<T, Promise<FlowTrigger<R>>> onEnter, Callable<Promise<Void>> onExit) {
        this.onEnter = onEnter == null ? defE() : onEnter;
        this.onExit = onExit == null ? defX() : onExit;
    }

    private Callable<Promise<Void>> defX() {
        return () -> null;
    }

    private static <T, R> Fun1Unckd<T, Promise<FlowTrigger<R>>> defE() {
        return t -> Promises.just(null);
    }
}
