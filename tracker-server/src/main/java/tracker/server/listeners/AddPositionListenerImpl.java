package tracker.server.listeners;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 7/11/2017.
 */
final public class AddPositionListenerImpl implements AddPositionListener {
    final NewPositionListener newPositionListener;

    public AddPositionListenerImpl(NewPositionListener newPositionListener) {
        Objects.requireNonNull(newPositionListener);
        this.newPositionListener = newPositionListener;
    }

    @Override
    public void handle(Message<JsonObject> event) {

        newPositionListener.listenTo(event.body(), event);
    }
}
