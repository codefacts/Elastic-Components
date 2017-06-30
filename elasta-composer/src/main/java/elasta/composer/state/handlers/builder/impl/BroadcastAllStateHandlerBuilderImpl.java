package elasta.composer.state.handlers.builder.impl;

import elasta.composer.ComposerUtils;
import elasta.composer.Events;
import elasta.composer.MessageBus;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.builder.BroadcastAllStateHandlerBuilder;
import elasta.composer.state.handlers.impl.BroadcastAllStateHandlerImpl;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonArray;

import java.util.Objects;

/**
 * Created by sohan on 5/21/2017.
 */
final public class BroadcastAllStateHandlerBuilderImpl implements BroadcastAllStateHandlerBuilder {
    final MessageBus messageBus;
    final String eventAddress;

    public BroadcastAllStateHandlerBuilderImpl(MessageBus messageBus, String broadcastAddress) {
        Objects.requireNonNull(messageBus);
        Objects.requireNonNull(broadcastAddress);
        this.messageBus = messageBus;
        this.eventAddress = broadcastAddress;
    }

    @Override
    public MsgEnterEventHandlerP<JsonArray, JsonArray> build() {
        return new BroadcastAllStateHandlerImpl(
            messageBus, eventAddress
        );
    }
}
