package elasta.composer.impl;

import com.google.common.collect.ImmutableListMultimap;
import elasta.composer.MessageBus;
import elasta.composer.model.request.UserModel;
import elasta.core.promise.intfs.Promise;
import elasta.eventbus.SimpleEventBus;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 6/29/2017.
 */
final public class MessageBusImpl implements MessageBus {
    final SimpleEventBus simpleEventBus;

    public MessageBusImpl(SimpleEventBus simpleEventBus) {
        Objects.requireNonNull(simpleEventBus);
        this.simpleEventBus = simpleEventBus;
    }

    @Override
    public MessageBusImpl send(Params params) {

        simpleEventBus.send(
            convert(params)
        );

        return this;
    }

    @Override
    public <T> Promise<Message<T>> sendAndReceive(Params params) {

        return simpleEventBus.sendAndReceive(
            convert(params)
        );
    }

    @Override
    public Promise<Message<JsonObject>> sendAndReceiveJsonObject(Params params) {

        return sendAndReceive(params);
    }

    @Override
    public Promise<Message<JsonArray>> sendAndReceiveJsonArray(Params params) {

        return sendAndReceive(params);
    }

    @Override
    public MessageBusImpl publish(Params params) {

        simpleEventBus.publish(
            convert(params)
        );

        return this;
    }

    private SimpleEventBus.Params convert(Params params) {

        final MultiMap headers = params.getOptions().getHeaders();

        if (headers == null) {

            params.getOptions().setHeaders(new VertxMultiMap(
                ImmutableListMultimap.of(UserModel.userId, params.getUserId())
            ));

        } else if (headers.get(UserModel.userId) == null) {

            params.getOptions().setHeaders(new VertxMultiMap(
                ImmutableListMultimap.<String, String>builder()
                    .putAll(headers.entries())
                    .put(UserModel.userId, params.getUserId())
                    .build()
            ));

        }

        return SimpleEventBus.Params.builder()
            .address(params.getAddress())
            .message(params.getMessage())
            .options(params.getOptions())
            .build();
    }
}
