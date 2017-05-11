package elasta.eventbus;

import elasta.core.intfs.Consumer1Unckd;
import io.vertx.core.eventbus.Message;

/**
 * Created by sohan on 5/9/2017.
 */
public interface EventHandler<T> extends Consumer1Unckd<Message<T>>, EventHandlerBase {

    void accept(Message<T> tMessage) throws Throwable;

    @Override
    default int code() {
        return EventHandlerCodes.eventHandlerCode;
    }
}
