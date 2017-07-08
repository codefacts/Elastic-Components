package elasta.composer;

import elasta.composer.ex.MessageBusException;
import elasta.composer.model.request.UserModel;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 6/29/2017.
 */
public interface MessageBus {

    MessageBus send(Params params);

    <T> Promise<Message<T>> sendAndReceive(Params params);

    Promise<Message<JsonObject>> sendAndReceiveJsonObject(Params params);

    Promise<Message<JsonArray>> sendAndReceiveJsonArray(Params params);

    MessageBus publish(Params params);

    @Value
    @Builder
    final class Params {
        final String address;
        final Object message;
        final String userId;
        final DeliveryOptions options;

        public Params(String address, Object message, String userId, DeliveryOptions options) {
            Objects.requireNonNull(address);
            Objects.requireNonNull(message);
            this.address = address;
            this.message = message;
            this.userId = (userId == null) ? userId(options.getHeaders()) : userId;
            this.options = (options == null) ? new DeliveryOptions() : options;
        }

        private String userId(MultiMap headers) {

            if (headers == null || headers.get(UserModel.userId) == null) {
                throw new MessageBusException("UserId must be present in message headers or in userId field");
            }

            return headers.get(UserModel.userId);
        }
    }
}
