package elasta.core.intfs;

import elasta.core.promise.intfs.Promise;

import java.util.function.BiFunction;

/**
 * Created by Jango on 11/7/2016.
 */
public interface Fun2Async<T1, T2, R> extends BiFunction<T1, T2, Promise<R>> {
    @Override
    Promise<R> apply(T1 t1, T2 t2);
}
