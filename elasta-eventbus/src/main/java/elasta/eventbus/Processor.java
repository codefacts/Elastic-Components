package elasta.eventbus;

import elasta.core.intfs.Fun1Unckd;
import io.vertx.core.eventbus.Message;

/**
 * Created by sohan on 5/9/2017.
 */
public interface Processor<T, R> extends Fun1Unckd<Message<T>, Message<R>>, EventHandlerBase {
    Message<R> apply(Message<T> tMessage) throws Throwable;

    @Override
    default int code() {
        return EventHandlerCodes.processorCode;
    }
}
