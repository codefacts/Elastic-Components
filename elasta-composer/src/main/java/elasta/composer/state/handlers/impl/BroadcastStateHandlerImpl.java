package elasta.composer.state.handlers.impl;

import elasta.composer.ComposerUtils;
import elasta.composer.Events;
import elasta.composer.MessageBus;
import elasta.composer.Msg;
import elasta.composer.state.handlers.BroadcastStateHandler;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.eventbus.DeliveryOptions;

import java.util.Objects;

/**
 * Created by sohan on 6/30/2017.
 */
final public class BroadcastStateHandlerImpl implements BroadcastStateHandler<Object, Object> {
    final MessageBus messageBus;
    final String eventAddress;

    public BroadcastStateHandlerImpl(MessageBus messageBus, String broadcastAddress) {
        Objects.requireNonNull(messageBus);
        Objects.requireNonNull(broadcastAddress);
        this.messageBus = messageBus;
        this.eventAddress = broadcastAddress;
    }

    @Override
    public Promise<StateTrigger<Msg<Object>>> handle(Msg<Object> msg) throws Throwable {
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
    }
}
