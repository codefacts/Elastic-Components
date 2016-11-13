package elasta.core.eventbus;

import elasta.core.utils.TypedMap;
import elasta.core.promise.intfs.Promise;

/**
 * Created by Jango on 11/5/2016.
 */
public interface Context {

    String event();

    TypedMap params();

    <R> Promise<R> next(Object t);
}
