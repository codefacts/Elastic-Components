package elasta.core.eventbus;

import elasta.core.utils.TypedMap;

/**
 * Created by Jango on 11/13/2016.
 */
public interface Filter<T> {
    boolean handle(T t, String event, TypedMap params);
}
