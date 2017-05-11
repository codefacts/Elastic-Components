package elasta.eventbus;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.eventbus.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public interface SimpleEventBus {

    SimpleEventBus send(String address, Object message);

    SimpleEventBus send(String address, Object message, DeliveryOptions options);

    <T> Promise<Message<T>> sendAndReceive(String address, Object message);

    <T> Promise<Message<T>> sendAndReceive(String address, Object message, DeliveryOptions options);

    Promise<Message<JsonObject>> sendAndReceiveJsonObject(String address, Object message);

    Promise<Message<JsonObject>> sendAndReceiveJsonObject(String address, Object message, DeliveryOptions options);

    Promise<Message<JsonArray>> sendAndReceiveJsonArray(String address, Object message);

    Promise<Message<JsonArray>> sendAndReceiveJsonArray(String address, Object message, DeliveryOptions options);

    SimpleEventBus publish(String address, Object message);

    SimpleEventBus publish(String address, Object message, DeliveryOptions options);
}
