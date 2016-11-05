package elasta.core.eventbus.impl;

import elasta.core.eventbus.Handler;
import elasta.core.eventbus.SimpleEventbus;
import elasta.core.promise.intfs.Promise;

/**
 * Created by Jango on 11/5/2016.
 */
public class SimpleEventbusImpl implements SimpleEventbus {
    @Override
    public <T, R> SimpleEventbus addListener(String event, Handler<T, R> handler) {
        return null;
    }

    @Override
    public <T, R> Promise<R> fire(String event, T t) {
        return null;
    }
}
