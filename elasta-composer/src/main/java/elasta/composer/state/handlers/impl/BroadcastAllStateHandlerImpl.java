package elasta.composer.state.handlers.impl;

import elasta.composer.ComposerUtils;
import elasta.composer.Events;
import elasta.composer.MessageBus;
import elasta.composer.Msg;
import elasta.composer.state.handlers.BroadcastAllStateHandler;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonArray;

import java.util.Objects;

/**
 * Created by sohan on 6/30/2017.
 */
final public class BroadcastAllStateHandlerImpl implements BroadcastAllStateHandler<JsonArray, JsonArray> {
    final MessageBus messageBus;
    final String eventAddress;

    public BroadcastAllStateHandlerImpl(MessageBus messageBus, String broadcastAddress) {
        Objects.requireNonNull(messageBus);
        Objects.requireNonNull(broadcastAddress);
        this.messageBus = messageBus;
        this.eventAddress = broadcastAddress;
    }

    @Override
    public Promise<StateTrigger<Msg<JsonArray>>> handle(Msg<JsonArray> msg) throws Throwable {
        msg.body().forEach(
            obj -> messageBus.publish(
                MessageBus.Params.builder()
                    .address(eventAddress)
                    .message(obj)
                    .options(
                        new DeliveryOptions()
                            .setHeaders(
                                ComposerUtils.toVertxMultimap(msg.headers().getMultimap())
                            )
                    )
                    .build()
            )
        );

        return Promises.of(
            Flow.trigger(Events.next, msg)
        );
    }
}
