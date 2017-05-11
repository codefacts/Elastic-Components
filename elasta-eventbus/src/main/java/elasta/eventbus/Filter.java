package elasta.eventbus;

import elasta.core.intfs.PredicateUnckd;
import io.vertx.core.eventbus.Message;

/**
 * Created by sohan on 5/9/2017.
 */
public interface Filter<T> extends PredicateUnckd<Message<T>>, EventHandlerBase {
    @Override
    boolean test(Message<T> message) throws Throwable;

    @Override
    default int code() {
        return EventHandlerCodes.filterCode;
    }
}
