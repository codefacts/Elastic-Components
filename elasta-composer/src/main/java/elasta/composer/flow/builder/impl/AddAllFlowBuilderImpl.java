package elasta.composer.flow.builder.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.States;
import elasta.composer.flow.builder.AddAllFlowBuilder;
import elasta.composer.state.handlers.*;
import elasta.core.flow.Flow;

import java.util.Objects;

/**
 * Created by sohan on 5/20/2017.
 */
final public class AddAllFlowBuilderImpl implements AddAllFlowBuilder {
    final StartStateHandler startHandler;
    final AuthorizeAllStateHandler authorizeAllHandler;
    final GenerateIdsAllStateHandler generateIdsAllHandler;
    final ValidateAllStateHandler validateAllHandler;
    final AddAllStateHandler addAllHandler;
    final BroadcastAllStateHandler broadcastAllHandler;
    final GenerateResponseStateHandler generateResponseHandler;
    final EndStateHandler endHandler;

    public AddAllFlowBuilderImpl(StartStateHandler startHandler, AuthorizeAllStateHandler authorizeAllHandler, GenerateIdsAllStateHandler generateIdsAllHandler,
                                 ValidateAllStateHandler validateAllHandler, AddAllStateHandler addAllHandler,
                                 BroadcastAllStateHandler broadcastAllHandler, GenerateResponseStateHandler generateResponseHandler, EndStateHandler endHandler) {
        Objects.requireNonNull(startHandler);
        Objects.requireNonNull(authorizeAllHandler);
        Objects.requireNonNull(generateIdsAllHandler);
        Objects.requireNonNull(validateAllHandler);
        Objects.requireNonNull(addAllHandler);
        Objects.requireNonNull(broadcastAllHandler);
        Objects.requireNonNull(generateResponseHandler);
        Objects.requireNonNull(endHandler);
        this.startHandler = startHandler;
        this.authorizeAllHandler = authorizeAllHandler;
        this.generateIdsAllHandler = generateIdsAllHandler;
        this.validateAllHandler = validateAllHandler;
        this.addAllHandler = addAllHandler;
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
                Flow.on(Events.next, States.generateIdsAll),
                Flow.on(Events.authorizationError, States.end)
            )
            .when(States.generateIdsAll, Flow.on(Events.next, States.validateAll))
            .when(
                States.validateAll,
                Flow.on(Events.next, States.insertAll),
                Flow.on(Events.validationError, States.end)
            )
            .when(States.insertAll, Flow.on(Events.next, States.broadcastAll))
            .when(States.broadcastAll, Flow.on(Events.next, States.generateResponse))
            .when(States.generateResponse, Flow.on(Events.next, States.end))
            .when(States.end, Flow.end())
            .handlersP(States.start, startHandler)
            .handlersP(States.authorizeAll, authorizeAllHandler)
            .handlersP(States.generateIdsAll, generateIdsAllHandler)
            .handlersP(States.validateAll, validateAllHandler)
            .handlersP(States.insertAll, addAllHandler)
            .handlersP(States.broadcastAll, broadcastAllHandler)
            .handlersP(States.generateResponse, generateResponseHandler)
            .handlersP(States.end, endHandler)
            .initialState(States.start)
            .build();
    }
}
