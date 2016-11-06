package elasta.core.promise.intfs;

import elasta.core.intfs.Fun1Unckd;

/**
 * Created by Jango on 11/3/2016.
 */
@FunctionalInterface
public interface DoOnErrorHandler<R> extends Fun1Unckd<Throwable, R> {

}
