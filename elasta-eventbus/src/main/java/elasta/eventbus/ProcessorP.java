package elasta.eventbus;

import elasta.core.intfs.Fun1Async;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.eventbus.Message;

/**
 * Created by sohan on 5/9/2017.
 */
public interface ProcessorP<T, R> extends Fun1Async<Message<T>, Message<R>>, EventHandlerBase {
    Promise<Message<R>> apply(Message<T> tMessage);

    @Override
    default int code() {
        return EventHandlerCodes.processorPCode;
    }
}
