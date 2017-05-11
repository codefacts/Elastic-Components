package elasta.eventbus.impl;

import elasta.eventbus.EventBusErrorHandler;
import elasta.eventbus.EventBusUtils;
import elasta.eventbus.EventHandlersPipe;
import io.vertx.core.eventbus.Message;

import java.util.Objects;

/**
 * Created by sohan on 5/10/2017.
 */
final public class RegisterEventHandlersPipeHelper {

    public RegisterEventHandlersPipeHelper() {
    }

    public void register(final EventBusUtils.RegisterHandlersParams params) {
        Objects.requireNonNull(params);
        params.getEventAddressToEventHandlersPipeMap().getMap().forEach((address, eventHandlersPipe) -> {
            params.getEventBus().consumer(address, message -> handleMessage(address, message, eventHandlersPipe, params));
        });
    }

    private <T, R> void handleMessage(String address, Message<T> message, EventHandlersPipe<T, R> trEventHandlersPipe, EventBusUtils.RegisterHandlersParams params) {

        try {

            trEventHandlersPipe.apply(message)
                .err2(
                    (throwable, o) -> params.getErrorHandler()
                        .handleError(
                            EventBusErrorHandler.HandleErrorParams.<T>builder()
                                .message(message)
                                .address(address)
                                .throwable(throwable)
                                .errorValue(message)
                                .build()
                        )
                )
            ;

        } catch (Exception ex) {
            params.getErrorHandler().handleError(
                EventBusErrorHandler.HandleErrorParams.<T>builder()
                    .message(message)
                    .address(address)
                    .throwable(ex)
                    .errorValue(message)
                    .build()
            );
        }
    }
}
