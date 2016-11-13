package elasta.core.flow;

import elasta.core.promise.intfs.Promise;

/**
 * Created by Jango on 11/13/2016.
 */
public interface EnterEventHandlerP<T, R> {
    Promise<StateTrigger<R>> handle(T t) throws Throwable;
}
