package elasta.core.promise.intfs;

import elasta.core.intfs.FunctionUnchecked;

/**
 * Created by Jango on 11/3/2016.
 */
@FunctionalInterface
public interface DoOnErrorHandler<R> extends FunctionUnchecked<Throwable, R> {

}
