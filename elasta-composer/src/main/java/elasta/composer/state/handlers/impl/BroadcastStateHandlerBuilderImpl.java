package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.state.handlers.BroadcastStateHandlerBuilder;
import elasta.core.flow.EnterEventHandlerP;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;
import elasta.eventbus.SimpleEventBus;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class BroadcastStateHandlerBuilderImpl implements BroadcastStateHandlerBuilder {
    final SimpleEventBus simpleEventBus;
    final String eventAddress;

    public BroadcastStateHandlerBuilderImpl(SimpleEventBus simpleEventBus, String eventAddress) {
        Objects.requireNonNull(simpleEventBus);
        Objects.requireNonNull(eventAddress);
        this.simpleEventBus = simpleEventBus;
        this.eventAddress = eventAddress;
    }

    @Override
    public EnterEventHandlerP build() {
        return request -> {

            simpleEventBus.publish(eventAddress, request);

            return Promises.of(
                Flow.trigger(Events.next, request)
            );
        };
    }
}
