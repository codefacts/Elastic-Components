package elasta.core.intfs;

import elasta.core.promise.intfs.Promise;

/**
 * Created by Jango on 11/13/2016.
 */
public interface PredicateAsync<T> {
    Promise<Boolean> test(T t) throws Throwable;
}
