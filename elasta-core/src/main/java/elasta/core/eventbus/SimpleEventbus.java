package elasta.core.eventbus;

import elasta.core.promise.intfs.Promise;

import java.util.Map;

/**
 * Created by Jango on 11/5/2016.
 */
public interface SimpleEventBus {

    <T, R> SimpleEventBus addListener(String event, Handler<T, R> handler);

    <R> Promise<R> fire(String event, Object t);

    <R> Promise<R> fire(String event, Object t, Map<String, ?> extra);
}
