package elasta.composer.state.handlers.impl;

import elasta.composer.*;
import elasta.composer.state.handlers.BroadcastStateHandlerBuilder;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;
import elasta.eventbus.SimpleEventBus;
import io.vertx.core.eventbus.DeliveryOptions;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class BroadcastStateHandlerBuilderImpl implements BroadcastStateHandlerBuilder {
    final SimpleEventBus simpleEventBus;
    final String eventAddress;

    public BroadcastStateHandlerBuilderImpl(SimpleEventBus simpleEventBus, String broadcastAddress) {
        Objects.requireNonNull(simpleEventBus);
        Objects.requireNonNull(broadcastAddress);
        this.simpleEventBus = simpleEventBus;
        this.eventAddress = broadcastAddress;
    }

    @Override
    public MsgEnterEventHandlerP<Object, Object> build() {
        return request -> {

            simpleEventBus.publish(
                eventAddress, request.body(),
                new DeliveryOptions()
                    .setHeaders(
                        ComposerUtils.toMultimap(request.headers().getMultimap())
                    )
            );

            return Promises.of(
                Flow.trigger(Events.next, request)
            );
        };
    }

}
