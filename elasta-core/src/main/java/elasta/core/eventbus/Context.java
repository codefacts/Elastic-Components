package elasta.core.eventbus;

import elasta.core.TypedMap;
import elasta.core.promise.intfs.Promise;

import java.util.Map;

/**
 * Created by Jango on 11/5/2016.
 */
public interface Context<T, R> {

    String event();

    TypedMap params();

    Promise<R> next(T t);
}
