package elasta.composer.flow.builder.impl;

import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.flow.builder.DeleteFlowBuilder;
import elasta.composer.Events;
import elasta.composer.States;
import elasta.core.flow.Flow;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class DeleteFlowBuilderImpl implements DeleteFlowBuilder {
    final MsgEnterEventHandlerP startHandler;
    final MsgEnterEventHandlerP authorizeHandler;
    final MsgEnterEventHandlerP deleteHandler;
    final MsgEnterEventHandlerP broadcastHandler;
    final MsgEnterEventHandlerP generateResponseHandler;
    final MsgEnterEventHandlerP endHandler;

    public DeleteFlowBuilderImpl(MsgEnterEventHandlerP startHandler, MsgEnterEventHandlerP authorizeHandler, MsgEnterEventHandlerP deleteHandler, MsgEnterEventHandlerP broadcastHandler, MsgEnterEventHandlerP generateResponseHandler, MsgEnterEventHandlerP endHandler) {
        Objects.requireNonNull(startHandler);
        Objects.requireNonNull(authorizeHandler);
        Objects.requireNonNull(deleteHandler);
        Objects.requireNonNull(broadcastHandler);
        Objects.requireNonNull(generateResponseHandler);
        Objects.requireNonNull(endHandler);
        this.startHandler = startHandler;
        this.authorizeHandler = authorizeHandler;
        this.deleteHandler = deleteHandler;
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
                Flow.on(Events.next, States.delete),
                Flow.on(Events.authorizationError, States.end)
            )
            .when(States.delete, Flow.on(Events.next, States.broadcast))
            .when(States.broadcast, Flow.on(Events.next, States.generateResponse))
            .when(States.generateResponse, Flow.on(Events.next, States.end))
            .when(States.end, Flow.end())
            .handlersP(States.start, startHandler)
            .handlersP(States.authorize, authorizeHandler)
            .handlersP(States.delete, deleteHandler)
            .handlersP(States.broadcast, broadcastHandler)
            .handlersP(States.generateResponse, generateResponseHandler)
            .handlersP(States.end, endHandler)
            .initialState(States.start)
            .build();
    }
}
