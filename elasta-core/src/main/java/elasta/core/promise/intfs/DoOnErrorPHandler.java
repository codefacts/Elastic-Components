package elasta.core.promise.intfs;

import elasta.core.intfs.Fun1Unckd;
import elasta.core.intfs.Fun2Unckd;

/**
 * Created by Jango on 11/3/2016.
 */
@FunctionalInterface
public interface DoOnErrorPHandler<P, T> extends Fun2Unckd<Throwable, P, Promise<T>> {
}
