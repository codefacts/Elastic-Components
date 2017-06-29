package elasta.composer.state.handlers.impl;

import elasta.composer.*;
import elasta.composer.state.handlers.BroadcastStateHandlerBuilder;
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
        return msg -> {

            messageBus.publish(
                MessageBus.Params.builder()
                    .address(eventAddress)
                    .message(msg.body())
                    .options(
                        new DeliveryOptions()
                            .setHeaders(
                                ComposerUtils.toVertxMultimap(msg.headers().getMultimap())
                            )
                    )
                    .build()
            );

            return Promises.of(
                Flow.trigger(Events.next, msg)
            );
        };
    }

}
