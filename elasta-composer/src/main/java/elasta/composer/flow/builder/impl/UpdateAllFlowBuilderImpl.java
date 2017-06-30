package elasta.composer.flow.builder.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.States;
import elasta.composer.flow.builder.UpdateAllFlowBuilder;
import elasta.composer.state.handlers.*;
import elasta.core.flow.Flow;

import java.util.Objects;

/**
 * Created by sohan on 5/20/2017.
 */
final public class UpdateAllFlowBuilderImpl implements UpdateAllFlowBuilder {
    final StartStateHandler startHandler;
    final AuthorizeAllStateHandler authorizeAllHandler;
    final GenerateIdsAllStateHandler generateIdsHandler;
    final ValidateAllStateHandler validateAllHandler;
    final UpdateAllStateHandler updateAllHandler;
    final BroadcastAllStateHandler broadcastAllHandler;
    final GenerateResponseStateHandler generateResponseHandler;
    final EndStateHandler endHandler;

    public UpdateAllFlowBuilderImpl(StartStateHandler startHandler,
                                    AuthorizeAllStateHandler authorizeAllHandler,
                                    GenerateIdsAllStateHandler generateIdsHandler,
                                    ValidateAllStateHandler validateAllHandler,
                                    UpdateAllStateHandler updateAllHandler,
                                    BroadcastAllStateHandler broadcastAllHandler,
                                    GenerateResponseStateHandler generateResponseHandler,
                                    EndStateHandler endHandler) {
        Objects.requireNonNull(startHandler);
        Objects.requireNonNull(authorizeAllHandler);
        Objects.requireNonNull(generateIdsHandler);
        Objects.requireNonNull(validateAllHandler);
        Objects.requireNonNull(updateAllHandler);
        Objects.requireNonNull(broadcastAllHandler);
        Objects.requireNonNull(generateResponseHandler);
        Objects.requireNonNull(endHandler);
        this.startHandler = startHandler;
        this.authorizeAllHandler = authorizeAllHandler;
        this.generateIdsHandler = generateIdsHandler;
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
                Flow.on(Events.next, States.generateIdsAll),
                Flow.on(Events.authorizationError, States.end)
            )
            .when(States.generateIdsAll, Flow.on(Events.next, States.validateAll))
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
            .handlersP(States.generateIdsAll, generateIdsHandler)
            .handlersP(States.validateAll, validateAllHandler)
            .handlersP(States.updateAll, updateAllHandler)
            .handlersP(States.broadcastAll, broadcastAllHandler)
            .handlersP(States.generateResponse, generateResponseHandler)
            .handlersP(States.end, endHandler)
            .initialState(States.start)
            .build();
    }
}
