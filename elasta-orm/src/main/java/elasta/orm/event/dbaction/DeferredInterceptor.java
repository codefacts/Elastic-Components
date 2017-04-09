package elasta.orm.event.dbaction;

import elasta.core.promise.intfs.Promise;

/**
 * Created by sohan on 4/6/2017.
 */
public interface DeferredInterceptor<T> {
    Promise<T> intercept(T t);
}
