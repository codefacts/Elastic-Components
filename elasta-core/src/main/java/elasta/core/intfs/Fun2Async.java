package elasta.core.intfs;

import elasta.core.promise.intfs.Promise;

/**
 * Created by Jango on 11/7/2016.
 */
public interface Fun2Async<T, R> {
    Promise<R> apply(T t);
}
