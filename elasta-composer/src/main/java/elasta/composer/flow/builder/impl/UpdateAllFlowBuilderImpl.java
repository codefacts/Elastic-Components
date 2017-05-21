package elasta.composer.flow.builder.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.States;
import elasta.composer.flow.builder.UpdateAllFlowBuilder;
import elasta.core.flow.Flow;

import java.util.Objects;

/**
 * Created by sohan on 5/20/2017.
 */
final public class UpdateAllFlowBuilderImpl implements UpdateAllFlowBuilder {
    final MsgEnterEventHandlerP startHandler;
    final MsgEnterEventHandlerP authorizeAllHandler;
    final MsgEnterEventHandlerP validateAllHandler;
    final MsgEnterEventHandlerP updateAllHandler;
    final MsgEnterEventHandlerP broadcastAllHandler;
    final MsgEnterEventHandlerP generateResponseHandler;
    final MsgEnterEventHandlerP endHandler;

    public UpdateAllFlowBuilderImpl(MsgEnterEventHandlerP startHandler, MsgEnterEventHandlerP authorizeAllHandler, MsgEnterEventHandlerP validateAllHandler, MsgEnterEventHandlerP updateAllHandler, MsgEnterEventHandlerP broadcastAllHandler, MsgEnterEventHandlerP generateResponseHandler, MsgEnterEventHandlerP endHandler) {
        Objects.requireNonNull(startHandler);
        Objects.requireNonNull(authorizeAllHandler);
        Objects.requireNonNull(validateAllHandler);
        Objects.requireNonNull(updateAllHandler);
        Objects.requireNonNull(broadcastAllHandler);
        Objects.requireNonNull(generateResponseHandler);
        Objects.requireNonNull(endHandler);
        this.startHandler = startHandler;
        this.authorizeAllHandler = authorizeAllHandler;
        this.validateAllHandler = validateAllHandler;
        this.updateAllHandler = updateAllHandler;
        this.broadcastAllHandler = broadcastAllHandler;
        this.generateResponseHandler = generateResponseHandler;
        this.endHandler = endHandler;
    }

    @Override
    public Flow build() {
        return Flow.builder()
            .when(States.start, Flow.on(Events.next, States.authorizeAll))
            .when(
                States.authorizeAll,
                Flow.on(Events.next, States.validateAll),
                Flow.on(Events.authorizationError, States.end)
            )
            .when(
                States.validateAll,
                Flow.on(Events.next, States.updateAll),
                Flow.on(Events.validationError, States.end)
            )
            .when(States.updateAll, Flow.on(Events.next, States.broadcastAll))
            .when(States.broadcastAll, Flow.on(Events.next, States.generateResponse))
            .when(States.generateResponse, Flow.on(Events.next, States.end))
            .when(States.end, Flow.end())
            .handlersP(States.start, startHandler)
            .handlersP(States.authorizeAll, authorizeAllHandler)
            .handlersP(States.validateAll, validateAllHandler)
            .handlersP(States.updateAll, updateAllHandler)
            .handlersP(States.broadcastAll, broadcastAllHandler)
            .handlersP(States.generateResponse, generateResponseHandler)
            .handlersP(States.end, endHandler)
            .initialState(States.start)
            .build();
    }
}
