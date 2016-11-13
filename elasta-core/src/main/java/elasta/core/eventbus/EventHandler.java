package elasta.core.eventbus;

import elasta.core.promise.intfs.Promise;
import elasta.core.utils.TypedMap;

/**
 * Created by Jango on 11/13/2016.
 */
public interface EventHandler<T> {
    void handle(T t, String event, TypedMap params);
}
