package elasta.webutils;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;

/**
 * Created by Jango on 9/12/2016.
 */
public interface EventHandler<T> extends Handler<Message<T>> {
    @Override
    public void handle(Message<T> event);
}
