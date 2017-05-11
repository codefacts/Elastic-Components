package elasta.eventbus;

import elasta.core.intfs.Fun1Async;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.eventbus.Message;

/**
 * Created by sohan on 5/9/2017.
 */
public interface EventHandlerP<T> extends Fun1Async<Message<T>, Void>, EventHandlerBase {
    Promise<Void> apply(Message<T> tMessage);

    @Override
    default int code() {
        return EventHandlerCodes.eventHandlerPCode;
    }
}
