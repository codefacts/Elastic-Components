package elasta.composer.state.handlers.builder.impl;

import elasta.composer.*;
import elasta.composer.state.handlers.builder.BroadcastStateHandlerBuilder;
import elasta.composer.state.handlers.impl.BroadcastStateHandlerImpl;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;
import io.vertx.core.eventbus.DeliveryOptions;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class BroadcastStateHandlerBuilderImpl implements BroadcastStateHandlerBuilder {
    final MessageBus messageBus;
    final String eventAddress;

    public BroadcastStateHandlerBuilderImpl(MessageBus messageBus, String broadcastAddress) {
        Objects.requireNonNull(messageBus);
        Objects.requireNonNull(broadcastAddress);
        this.messageBus = messageBus;
        this.eventAddress = broadcastAddress;
    }

    @Override
    public MsgEnterEventHandlerP<Object, Object> build() {
        return new BroadcastStateHandlerImpl(
            messageBus, eventAddress
        );
    }

}
