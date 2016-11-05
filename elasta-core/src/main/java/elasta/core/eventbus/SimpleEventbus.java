package elasta.core.eventbus;

import elasta.core.promise.intfs.Promise;

/**
 * Created by Jango on 11/5/2016.
 */
public interface SimpleEventbus {
    
    <T, R> SimpleEventbus addListener(String event, Handler<T, R> handler);

    <T, R> Promise<R> fire(String event, T t);
}
