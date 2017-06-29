package elasta.eventbus;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.eventbus.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

public interface SimpleEventBus {

    SimpleEventBus send(Params params);

    <T> Promise<Message<T>> sendAndReceive(Params params);

    Promise<Message<JsonObject>> sendAndReceiveJsonObject(Params params);

    Promise<Message<JsonArray>> sendAndReceiveJsonArray(Params params);

    SimpleEventBus publish(Params params);

    @Value
    @Builder
    public final class Params {
        final String address;
        final Object message;
        final DeliveryOptions options;

        public Params(String address, Object message, DeliveryOptions options) {
            Objects.requireNonNull(address);
            Objects.requireNonNull(message);
            this.address = address;
            this.message = message;
            this.options = (options == null) ? new DeliveryOptions() : options;
        }
    }
}
