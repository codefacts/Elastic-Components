package elasta.promise.impl;

import elasta.promise.intfs.Signal;

/**
 * Created by Jango on 8/25/2016.
 */
public interface DeferredExecutor<T, R> extends Executor<T, PromiseImpl<SignalImpl<R>>> {

    default int type() {
        return DEFERRED;
    }
}
