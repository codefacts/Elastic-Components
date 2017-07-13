package tracker.server.listeners;

import elasta.composer.Msg;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 7/13/2017.
 */
public interface NewPositionListener {
    void listenTo(JsonObject position, Message msg);
}
