package elasta.eventbus;

import elasta.core.intfs.PredicateAsync;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.eventbus.Message;

/**
 * Created by sohan on 5/9/2017.
 */
public interface FilterP<T> extends PredicateAsync<Message<T>>, EventHandlerBase {
    Promise<Boolean> test(Message<T> tMessage);

    @Override
    default int code() {
        return EventHandlerCodes.filterPCode;
    }
}
