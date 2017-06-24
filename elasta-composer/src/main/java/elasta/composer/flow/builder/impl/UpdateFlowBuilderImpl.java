package elasta.composer.flow.builder.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.States;
import elasta.composer.flow.builder.UpdateFlowBuilder;
import elasta.core.flow.Flow;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class UpdateFlowBuilderImpl implements UpdateFlowBuilder {
    final MsgEnterEventHandlerP startHandler;
    final MsgEnterEventHandlerP authorizeHandler;
    final MsgEnterEventHandlerP validateHandler;
    final MsgEnterEventHandlerP updateHandler;
    final MsgEnterEventHandlerP broadcastHandler;
    final MsgEnterEventHandlerP generateResponseHandler;
    final MsgEnterEventHandlerP endHandler;

    public UpdateFlowBuilderImpl(MsgEnterEventHandlerP startHandler, MsgEnterEventHandlerP authorizeHandler, MsgEnterEventHandlerP validateHandler, MsgEnterEventHandlerP updateHandler, MsgEnterEventHandlerP broadcastHandler, MsgEnterEventHandlerP generateResponseHandler, MsgEnterEventHandlerP endHandler) {
        Objects.requireNonNull(startHandler);
        Objects.requireNonNull(authorizeHandler);
        Objects.requireNonNull(validateHandler);
        Objects.requireNonNull(updateHandler);
        Objects.requireNonNull(broadcastHandler);
        Objects.requireNonNull(generateResponseHandler);
        Objects.requireNonNull(endHandler);
        this.startHandler = startHandler;
        this.authorizeHandler = authorizeHandler;
        this.validateHandler = validateHandler;
        this.updateHandler = updateHandler;
        this.broadcastHandler = broadcastHandler;
        this.generateResponseHandler = generateResponseHandler;
        this.endHandler = endHandler;
    }

    @Override
    public Flow build() {
        return Flow.builder()
            .when(States.start, Flow.on(Events.next, States.authorize))
            .when(
                States.authorize,
                Flow.on(Events.next, States.validate),
                Flow.on(Events.authorizationError, States.end)
            )
            .when(
                States.validate,
                Flow.on(Events.next, States.update),
                Flow.on(Events.validationError, States.end)
            )
            .when(States.update, Flow.on(Events.next, States.broadcast))
            .when(States.broadcast, Flow.on(Events.next, States.generateResponse))
            .when(States.generateResponse, Flow.on(Events.next, States.end))
            .when(States.end, Flow.end())
            .handlersP(States.start, startHandler)
            .handlersP(States.authorize, authorizeHandler)
            .handlersP(States.validate, validateHandler)
            .handlersP(States.update, updateHandler)
            .handlersP(States.broadcast, broadcastHandler)
            .handlersP(States.generateResponse, generateResponseHandler)
            .handlersP(States.end, endHandler)
            .initialState(States.start)
            .build();
    }
}