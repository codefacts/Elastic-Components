package elasta.composer.flow.builder.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.States;
import elasta.composer.flow.builder.FindAllChildFlowBuilder;
import elasta.core.flow.Flow;

import java.util.Objects;

/**
 * Created by sohan on 5/28/2017.
 */
final public class FindAllChildFlowBuilderImpl implements FindAllChildFlowBuilder {
    final MsgEnterEventHandlerP startHandler;
    final MsgEnterEventHandlerP authorizeHandler;
    final MsgEnterEventHandlerP queryAllChildConversionToCriteriaHandler;
    final MsgEnterEventHandlerP queryAllChildFindAllHandler;
    final MsgEnterEventHandlerP endHandler;

    public FindAllChildFlowBuilderImpl(MsgEnterEventHandlerP startHandler, MsgEnterEventHandlerP authorizeHandler, MsgEnterEventHandlerP queryAllChildConversionToCriteriaHandler, MsgEnterEventHandlerP queryAllChildFindAllHandler, MsgEnterEventHandlerP endHandler) {
        Objects.requireNonNull(startHandler);
        Objects.requireNonNull(authorizeHandler);
        Objects.requireNonNull(queryAllChildConversionToCriteriaHandler);
        Objects.requireNonNull(queryAllChildFindAllHandler);
        Objects.requireNonNull(endHandler);
        this.startHandler = startHandler;
        this.authorizeHandler = authorizeHandler;
        this.queryAllChildConversionToCriteriaHandler = queryAllChildConversionToCriteriaHandler;
        this.queryAllChildFindAllHandler = queryAllChildFindAllHandler;
        this.endHandler = endHandler;
    }

    @Override
    public Flow build() {
        return Flow.builder()
            .when(States.start, Flow.on(Events.next, States.authorize))
            .when(
                States.authorize,
                Flow.on(Events.next, States.conversionToCriteria),
                Flow.on(Events.authorizationError, States.end)
            )
            .when(States.conversionToCriteria, Flow.on(Events.next, States.findAll))
            .when(States.findAll, Flow.on(Events.next, States.end))
            .when(States.end, Flow.end())
            .handlersP(States.start, startHandler)
            .handlersP(States.authorize, authorizeHandler)
            .handlersP(States.conversionToCriteria, queryAllChildConversionToCriteriaHandler)
            .handlersP(States.findAll, queryAllChildFindAllHandler)
            .handlersP(States.end, endHandler)
            .initialState(States.start)
            .build();
    }
}
