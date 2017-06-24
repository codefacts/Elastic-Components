package elasta.composer.flow.builder.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.States;
import elasta.composer.flow.builder.FindOneChildFlowBuilder;
import elasta.core.flow.Flow;

import java.util.Objects;

/**
 * Created by sohan on 5/28/2017.
 */
final public class FindOneChildFlowBuilderImpl implements FindOneChildFlowBuilder {
    final MsgEnterEventHandlerP startHandler;
    final MsgEnterEventHandlerP authorizeHandler;
    final MsgEnterEventHandlerP queryChildConversionToCriteriaHandler;
    final MsgEnterEventHandlerP queryChildFindOneHandler;
    final MsgEnterEventHandlerP endHandler;

    public FindOneChildFlowBuilderImpl(MsgEnterEventHandlerP startHandler, MsgEnterEventHandlerP authorizeHandler, MsgEnterEventHandlerP queryChildConversionToCriteriaHandler, MsgEnterEventHandlerP queryChildFindOneHandler, MsgEnterEventHandlerP endHandler) {
        Objects.requireNonNull(startHandler);
        Objects.requireNonNull(authorizeHandler);
        Objects.requireNonNull(queryChildConversionToCriteriaHandler);
        Objects.requireNonNull(queryChildFindOneHandler);
        Objects.requireNonNull(endHandler);
        this.startHandler = startHandler;
        this.authorizeHandler = authorizeHandler;
        this.queryChildConversionToCriteriaHandler = queryChildConversionToCriteriaHandler;
        this.queryChildFindOneHandler = queryChildFindOneHandler;
        this.endHandler = endHandler;
    }

    @Override
    public Flow build() {
        return Flow.builder()
            .when(States.start, Flow.on(Events.next, States.authorize))
            .when(
                States.authorize,
                Flow.on(Events.next, States.conversionToCriteria)
            )
            .when(States.conversionToCriteria, Flow.on(Events.next, States.findOne))
            .when(States.findOne, Flow.on(Events.next, States.end))
            .when(States.end, Flow.end())
            .handlersP(States.start, startHandler)
            .handlersP(States.authorize, authorizeHandler)
            .handlersP(States.conversionToCriteria, queryChildConversionToCriteriaHandler)
            .handlersP(States.findOne, queryChildFindOneHandler)
            .handlersP(States.end, endHandler)
            .initialState(States.start)
            .build();
    }
}
