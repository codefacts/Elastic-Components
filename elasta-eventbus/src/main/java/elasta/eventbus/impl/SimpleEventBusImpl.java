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

    public SimpleEventBus send(String address, Object message) {
        eventBus.send(address, message);
        return this;
    }

    public SimpleEventBus send(String address, Object message, DeliveryOptions options) {
        eventBus.send(address, message, options);
        return this;
    }

    public <T> Promise<Message<T>> sendAndReceive(String address, Object message) {
        return Promises.exec(messageDefer -> eventBus.<T>send(address, message, deferred(messageDefer, message)));
    }

    public <T> Promise<Message<T>> sendAndReceive(String address, Object message, DeliveryOptions options) {
        return Promises.exec(messageDefer -> eventBus.send(address, message, options, deferred(messageDefer, message)));
    }

    @Override
    public Promise<Message<JsonObject>> sendAndReceiveJsonObject(String address, Object message) {
        return sendAndReceive(address, message);
    }

    @Override
    public Promise<Message<JsonObject>> sendAndReceiveJsonObject(String address, Object message, DeliveryOptions options) {
        return sendAndReceive(address, message, options);
    }

    @Override
    public Promise<Message<JsonArray>> sendAndReceiveJsonArray(String address, Object message) {
        return sendAndReceive(address, message);
    }

    @Override
    public Promise<Message<JsonArray>> sendAndReceiveJsonArray(String address, Object message, DeliveryOptions options) {
        return sendAndReceive(address, message, options);
    }

    public SimpleEventBus publish(String address, Object message) {
        eventBus.publish(address, message);
        return this;
    }

    public SimpleEventBus publish(String address, Object message, DeliveryOptions options) {
        eventBus.publish(address, message, options);
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
