package elasta.promise.impl;

/**
 * Created by Jango on 8/25/2016.
 */
public interface Executor<T, R> {
    int INSTANT = 1;
    int DEFERRED = 2;

    R execute(SignalImpl<T> signalImpl);

    default int type() {
        return 0;
    }
}
