package elasta.composer.flow.builder.impl;

import elasta.composer.Events;
import elasta.composer.States;
import elasta.composer.flow.builder.FindAllFlowBuilder;
import elasta.composer.state.handlers.*;
import elasta.core.flow.Flow;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class FindAllFlowBuilderImpl implements FindAllFlowBuilder {
    final StartStateHandler startHandler;
    final AuthorizeStateHandler authorizeHandler;
    final BeforeFindAllStateHandler beforeFindAllHandler;
    final FindAllStateHandler findAllHandler;
    final EndStateHandler endHandler;

    public FindAllFlowBuilderImpl(
        StartStateHandler startHandler,
        AuthorizeStateHandler authorizeHandler,
        BeforeFindAllStateHandler beforeFindAllHandler, FindAllStateHandler findAllHandler,
        EndStateHandler endHandler) {

        Objects.requireNonNull(startHandler);
        Objects.requireNonNull(authorizeHandler);
        Objects.requireNonNull(beforeFindAllHandler);
        Objects.requireNonNull(findAllHandler);
        Objects.requireNonNull(endHandler);
        this.startHandler = startHandler;
        this.authorizeHandler = authorizeHandler;
        this.beforeFindAllHandler = beforeFindAllHandler;
        this.findAllHandler = findAllHandler;
        this.endHandler = endHandler;
    }

    @Override
    public Flow build() {
        return Flow.builder()
            .when(States.start, Flow.on(Events.next, States.authorize))
            .when(
                States.authorize,
                Flow.on(Events.next, States.beforeFindAll),
                Flow.on(Events.authorizationError, States.end)
            )
            .when(States.beforeFindAll, Flow.on(Events.next, States.findAll))
            .when(States.findAll, Flow.on(Events.next, States.end))
            .when(States.end, Flow.end())
            .handlersP(States.start, startHandler)
            .handlersP(States.authorize, authorizeHandler)
            .handlersP(States.beforeFindAll, beforeFindAllHandler)
            .handlersP(States.findAll, findAllHandler)
            .handlersP(States.end, endHandler)
            .initialState(States.start)
            .build();
    }
}
