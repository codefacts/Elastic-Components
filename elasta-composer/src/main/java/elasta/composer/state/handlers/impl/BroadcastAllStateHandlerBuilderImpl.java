package elasta.composer.state.handlers.impl;

import elasta.composer.ComposerUtils;
import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.BroadcastAllStateHandlerBuilder;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;
import elasta.eventbus.SimpleEventBus;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonArray;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 5/21/2017.
 */
final public class BroadcastAllStateHandlerBuilderImpl implements BroadcastAllStateHandlerBuilder {
    final SimpleEventBus simpleEventBus;
    final String eventAddress;

    public BroadcastAllStateHandlerBuilderImpl(SimpleEventBus simpleEventBus, String broadcastAddress) {
        Objects.requireNonNull(simpleEventBus);
        Objects.requireNonNull(broadcastAddress);
        this.simpleEventBus = simpleEventBus;
        this.eventAddress = broadcastAddress;
    }

    @Override
    public MsgEnterEventHandlerP<JsonArray, JsonArray> build() {
        return msg -> {

            msg.body().forEach(
                obj -> simpleEventBus.publish(
                    eventAddress, obj,
                    new DeliveryOptions()
                        .setHeaders(
                            ComposerUtils.toVertxMultimap(msg.headers().getMultimap())
                        )
                )
            );

            return Promises.of(
                Flow.trigger(Events.next, msg)
            );
        };
    }
}
