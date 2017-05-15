package elasta.composer.flow.builder.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.flow.builder.InsertFlowBuilder;
import elasta.composer.States;
import elasta.core.flow.Flow;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class InsertFlowBuilderImpl implements InsertFlowBuilder {
    final MsgEnterEventHandlerP startHandler;
    final MsgEnterEventHandlerP authorizationHandler;
    final MsgEnterEventHandlerP idGenerationHandler;
    final MsgEnterEventHandlerP validationHandler;
    final MsgEnterEventHandlerP insertHandler;
    final MsgEnterEventHandlerP broadcastHandler;
    final MsgEnterEventHandlerP generateResponseHandler;
    final MsgEnterEventHandlerP endHandler;

    public InsertFlowBuilderImpl(MsgEnterEventHandlerP startHandler, MsgEnterEventHandlerP authorizationHandler, MsgEnterEventHandlerP idGenerationHandler, MsgEnterEventHandlerP validationHandler, MsgEnterEventHandlerP insertHandler, MsgEnterEventHandlerP broadcastHandler, MsgEnterEventHandlerP generateResponseHandler, MsgEnterEventHandlerP endHandler) {
        Objects.requireNonNull(startHandler);
        Objects.requireNonNull(authorizationHandler);
        Objects.requireNonNull(idGenerationHandler);
        Objects.requireNonNull(validationHandler);
        Objects.requireNonNull(insertHandler);
        Objects.requireNonNull(broadcastHandler);
        Objects.requireNonNull(generateResponseHandler);
        Objects.requireNonNull(endHandler);
        this.startHandler = startHandler;
        this.authorizationHandler = authorizationHandler;
        this.idGenerationHandler = idGenerationHandler;
        this.validationHandler = validationHandler;
        this.insertHandler = insertHandler;
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
                Flow.on(Events.next, States.idGeneration),
                Flow.on(Events.authorizationError, States.end)
            )
            .when(States.idGeneration, Flow.on(Events.next, States.validation))
            .when(
                States.validation,
                Flow.on(Events.next, States.insert),
                Flow.on(Events.validationError, States.end)
            )
            .when(States.insert, Flow.on(Events.next, States.broadcast))
            .when(States.broadcast, Flow.on(Events.next, States.generateResponse))
            .when(States.generateResponse, Flow.on(Events.next, States.end))
            .when(States.end, Flow.end())
            .handlersP(States.start, startHandler)
            .handlersP(States.authorization, authorizationHandler)
            .handlersP(States.idGeneration, idGenerationHandler)
            .handlersP(States.validation, validationHandler)
            .handlersP(States.insert, insertHandler)
            .handlersP(States.broadcast, broadcastHandler)
            .handlersP(States.generateResponse, generateResponseHandler)
            .handlersP(States.end, endHandler)
            .build();
    }
}
