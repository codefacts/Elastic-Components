package elasta.vertxutils;

import io.vertx.core.eventbus.Message;

/**
 * Created by Jango on 9/14/2016.
 */
public interface HandlerUtils {
    <T, R> void handleMessage(Message<T> message, VertxMessageHandler<T, R> handler);
}
