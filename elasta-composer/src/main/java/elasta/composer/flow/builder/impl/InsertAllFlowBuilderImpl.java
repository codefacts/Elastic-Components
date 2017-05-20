package elasta.composer.flow.builder.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.States;
import elasta.composer.flow.builder.InsertAllFlowBuilder;
import elasta.core.flow.Flow;

import java.util.Objects;

/**
 * Created by sohan on 5/20/2017.
 */
final public class InsertAllFlowBuilderImpl implements InsertAllFlowBuilder {
    final MsgEnterEventHandlerP startHandler;
    final MsgEnterEventHandlerP authorizeAllHandler;
    final MsgEnterEventHandlerP idGenerationHandler;
    final MsgEnterEventHandlerP validateAllHandler;
    final MsgEnterEventHandlerP insertAllHandler;
    final MsgEnterEventHandlerP broadcastHandler;
    final MsgEnterEventHandlerP generateResponseHandler;
    final MsgEnterEventHandlerP endHandler;

    public InsertAllFlowBuilderImpl(MsgEnterEventHandlerP startHandler, MsgEnterEventHandlerP authorizeAllHandler, MsgEnterEventHandlerP idGenerationHandler, MsgEnterEventHandlerP validateAllHandler, MsgEnterEventHandlerP insertAllHandler, MsgEnterEventHandlerP broadcastHandler, MsgEnterEventHandlerP generateResponseHandler, MsgEnterEventHandlerP endHandler) {
        Objects.requireNonNull(startHandler);
        Objects.requireNonNull(authorizeAllHandler);
        Objects.requireNonNull(idGenerationHandler);
        Objects.requireNonNull(validateAllHandler);
        Objects.requireNonNull(insertAllHandler);
        Objects.requireNonNull(broadcastHandler);
        Objects.requireNonNull(generateResponseHandler);
        Objects.requireNonNull(endHandler);
        this.startHandler = startHandler;
        this.authorizeAllHandler = authorizeAllHandler;
        this.idGenerationHandler = idGenerationHandler;
        this.validateAllHandler = validateAllHandler;
        this.insertAllHandler = insertAllHandler;
        this.broadcastHandler = broadcastHandler;
        this.generateResponseHandler = generateResponseHandler;
        this.endHandler = endHandler;
    }

    @Override
    public Flow build() {
        return Flow.builder()
            .when(States.start, Flow.on(Events.next, States.authorizeAll))
            .when(
                States.authorizeAll,
                Flow.on(Events.next, States.idGeneration),
                Flow.on(Events.authorizationError, States.end)
            )
            .when(States.idGeneration, Flow.on(Events.next, States.validateAll))
            .when(
                States.validateAll,
                Flow.on(Events.next, States.insertAll),
                Flow.on(Events.validationError, States.end)
            )
            .when(States.insertAll, Flow.on(Events.next, States.broadcast))
            .when(States.broadcast, Flow.on(Events.next, States.generateResponse))
            .when(States.generateResponse, Flow.on(Events.next, States.end))
            .when(States.end, Flow.end())
            .handlersP(States.start, startHandler)
            .handlersP(States.authorizeAll, authorizeAllHandler)
            .handlersP(States.idGeneration, idGenerationHandler)
            .handlersP(States.validateAll, validateAllHandler)
            .handlersP(States.insertAll, insertAllHandler)
            .handlersP(States.broadcast, broadcastHandler)
            .handlersP(States.generateResponse, generateResponseHandler)
            .handlersP(States.end, endHandler)
            .initialState(States.start)
            .build();
    }
}
