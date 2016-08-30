package elasta.promise.impl;

import elasta.promise.intfs.Signal;

/**
 * Created by Jango on 8/25/2016.
 */
public interface InstantExecutor<T, R> extends Executor<T, SignalImpl<R>> {

    default int type() {
        return INSTANT;
    }
}
