package elasta.core.eventbus;

import elasta.core.promise.intfs.Promise;

import java.util.Map;

/**
 * Created by Jango on 11/5/2016.
 */
public interface Handler<T, R> {
    Promise<R> handle(T t, Context<Object, R> context);
}
