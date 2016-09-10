package elasta.vertxutils;

import io.vertx.core.eventbus.Message;

/**
 * Created by Jango on 9/11/2016.
 */
public interface ReplyHandler<T> {
    void handleReply(Message message, T val);
}
