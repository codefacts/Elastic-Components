package elasta.composer.message.handlers;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

/**
 * Created by sohan on 5/14/2017.
 */
public interface MessageHandler<T> extends Handler<Message<T>> {
    @Override
    void handle(Message<T> event);
}
