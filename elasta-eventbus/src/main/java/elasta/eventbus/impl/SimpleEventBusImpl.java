package elasta.eventbus.impl;

import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Defer;
import elasta.core.promise.intfs.Promise;
import elasta.eventbus.SimpleEventBus;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 5/9/2017.
 */
final public class SimpleEventBusImpl implements SimpleEventBus {

    private final EventBus eventBus;

    public SimpleEventBusImpl(EventBus eventBus) {
        Objects.requireNonNull(eventBus);
        this.eventBus = eventBus;
    }

    public SimpleEventBus send(Params params) {
        eventBus.send(params.getAddress(), params.getMessage(), params.getOptions());
        return this;
    }

    public <T> Promise<Message<T>> sendAndReceive(Params params) {
        final Object message = params.getMessage();
        return Promises.exec(messageDefer -> eventBus.send(params.getAddress(), message, params.getOptions(), deferred(messageDefer, message)));
    }

    @Override
    public Promise<Message<JsonObject>> sendAndReceiveJsonObject(Params params) {
        return sendAndReceive(params);
    }

    @Override
    public Promise<Message<JsonArray>> sendAndReceiveJsonArray(Params params) {
        return sendAndReceive(params);
    }

    public SimpleEventBus publish(Params params) {
        eventBus.publish(params.getAddress(), params.getMessage(), params.getOptions());
        return this;
    }

    private <T> AsyncResultHandler<Message<T>> deferred(Defer<Message<T>> messageDefer, Object message) {
        return event -> {
            if (event.failed()) {
                messageDefer.reject(event.cause(), message);
                return;
            }
            messageDefer.resolve(event.result());
        };
    }
}
