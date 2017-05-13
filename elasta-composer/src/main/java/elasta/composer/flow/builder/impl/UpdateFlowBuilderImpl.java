package elasta.composer.flow.builder.impl;

import elasta.composer.Events;
import elasta.composer.States;
import elasta.composer.flow.builder.UpdateFlowBuilder;
import elasta.core.flow.EnterEventHandlerP;
import elasta.core.flow.Flow;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class UpdateFlowBuilderImpl implements UpdateFlowBuilder {
    final EnterEventHandlerP startHandler;
    final EnterEventHandlerP authorizationHandler;
    final EnterEventHandlerP validationHandler;
    final EnterEventHandlerP updateHandler;
    final EnterEventHandlerP broadcastHandler;
    final EnterEventHandlerP generateResponseHandler;
    final EnterEventHandlerP endHandler;

    public UpdateFlowBuilderImpl(EnterEventHandlerP startHandler, EnterEventHandlerP authorizationHandler, EnterEventHandlerP validationHandler, EnterEventHandlerP updateHandler, EnterEventHandlerP broadcastHandler, EnterEventHandlerP generateResponseHandler, EnterEventHandlerP endHandler) {
        Objects.requireNonNull(startHandler);
        Objects.requireNonNull(authorizationHandler);
        Objects.requireNonNull(validationHandler);
        Objects.requireNonNull(updateHandler);
        Objects.requireNonNull(broadcastHandler);
        Objects.requireNonNull(generateResponseHandler);
        Objects.requireNonNull(endHandler);
        this.startHandler = startHandler;
        this.authorizationHandler = authorizationHandler;
        this.validationHandler = validationHandler;
        this.updateHandler = updateHandler;
        this.broadcastHandler = broadcastHandler;
        this.generateResponseHandler = generateResponseHandler;
        this.endHandler = endHandler;
    }

    @Override
    public Flow build() {
        return Flow.builder()
            .when(States.start, Flow.on(Events.next, States.authorization))
            .when(
                States.authorization,
                Flow.on(Events.next, States.validation),
                Flow.on(Events.authorizationError, States.end)
            )
            .when(
                States.validation,
                Flow.on(Events.next, States.update),
                Flow.on(Events.validationError, States.end)
            )
            .when(States.update, Flow.on(Events.next, States.broadcast))
            .when(States.broadcast, Flow.on(Events.next, States.end))
            .when(States.end, Flow.end())
            .handlersP(States.start, startHandler)
            .handlersP(States.authorization, authorizationHandler)
            .handlersP(States.validation, validationHandler)
            .handlersP(States.update, updateHandler)
            .handlersP(States.broadcast, broadcastHandler)
            .handlersP(States.end, endHandler)
            .build();
    }
}
