package elasta.composer.flow.builder.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.flow.builder.AddFlowBuilder;
import elasta.composer.States;
import elasta.core.flow.Flow;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class AddFlowBuilderImpl implements AddFlowBuilder {
    final MsgEnterEventHandlerP startHandler;
    final MsgEnterEventHandlerP authorizeHandler;
    final MsgEnterEventHandlerP idGenerationHandler;
    final MsgEnterEventHandlerP validateHandler;
    final MsgEnterEventHandlerP insertHandler;
    final MsgEnterEventHandlerP broadcastHandler;
    final MsgEnterEventHandlerP generateResponseHandler;
    final MsgEnterEventHandlerP endHandler;

    public AddFlowBuilderImpl(MsgEnterEventHandlerP startHandler, MsgEnterEventHandlerP authorizeHandler, MsgEnterEventHandlerP idGenerationHandler, MsgEnterEventHandlerP validateHandler, MsgEnterEventHandlerP insertHandler, MsgEnterEventHandlerP broadcastHandler, MsgEnterEventHandlerP generateResponseHandler, MsgEnterEventHandlerP endHandler) {
        Objects.requireNonNull(startHandler);
        Objects.requireNonNull(authorizeHandler);
        Objects.requireNonNull(idGenerationHandler);
        Objects.requireNonNull(validateHandler);
        Objects.requireNonNull(insertHandler);
        Objects.requireNonNull(broadcastHandler);
        Objects.requireNonNull(generateResponseHandler);
        Objects.requireNonNull(endHandler);
        this.startHandler = startHandler;
        this.authorizeHandler = authorizeHandler;
        this.idGenerationHandler = idGenerationHandler;
        this.validateHandler = validateHandler;
        this.insertHandler = insertHandler;
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
                Flow.on(Events.next, States.idGeneration),
                Flow.on(Events.authorizationError, States.end)
            )
            .when(States.idGeneration, Flow.on(Events.next, States.validate))
            .when(
                States.validate,
                Flow.on(Events.next, States.insert),
                Flow.on(Events.validationError, States.end)
            )
            .when(States.insert, Flow.on(Events.next, States.broadcast))
            .when(States.broadcast, Flow.on(Events.next, States.generateResponse))
            .when(States.generateResponse, Flow.on(Events.next, States.end))
            .when(States.end, Flow.end())
            .handlersP(States.start, startHandler)
            .handlersP(States.authorize, authorizeHandler)
            .handlersP(States.idGeneration, idGenerationHandler)
            .handlersP(States.validate, validateHandler)
            .handlersP(States.insert, insertHandler)
            .handlersP(States.broadcast, broadcastHandler)
            .handlersP(States.generateResponse, generateResponseHandler)
            .handlersP(States.end, endHandler)
            .initialState(States.start)
            .build();
    }
}
