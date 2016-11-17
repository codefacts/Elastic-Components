package elasta.core.eventbus;

import elasta.core.promise.intfs.Promise;
import elasta.core.utils.TypedMap;

import java.util.Map;

/**
 * Created by Jango on 11/13/2016.
 */
public interface Processor<T, R> {
    R handle(T t, String event, TypedMap params);
}
