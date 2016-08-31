package elasta.composer.endpoints;

import io.vertx.core.eventbus.Message;

/**
 * Created by shahadat on 3/3/16.
 */
public interface Endpoint<T> {
    void process(Message<T> message);
}
