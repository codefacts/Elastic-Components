package elasta.core.promise.impl;

/**
 * Created by Jango on 8/25/2016.
 */
public interface DeferredExecutor<T, R> extends Executor<T, PromiseImpl<R>> {

    default int type() {
        return DEFERRED;
    }
}
