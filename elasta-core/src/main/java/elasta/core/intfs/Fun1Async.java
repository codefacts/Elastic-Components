package elasta.core.intfs;

import elasta.core.promise.intfs.Promise;

/**
 * Created by Jango on 11/6/2016.
 */
public interface Fun1Async<T, R> {
    Promise<R> apply(T t);
}
