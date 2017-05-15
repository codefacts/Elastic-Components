package elasta.composer.flow.builder.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.States;
import elasta.composer.flow.builder.FindOneFlowBuilder;
import elasta.core.flow.EnterEventHandler;
import elasta.core.flow.Flow;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class FindOneFlowBuilderImpl implements FindOneFlowBuilder {
    final MsgEnterEventHandlerP startHandler;
    final MsgEnterEventHandlerP authorizationHandler;
    final MsgEnterEventHandlerP conversionToCriteriaHandler;
    final MsgEnterEventHandlerP findOneHandler;
    final MsgEnterEventHandlerP endHandler;

    public FindOneFlowBuilderImpl(MsgEnterEventHandlerP startHandler, MsgEnterEventHandlerP authorizationHandler, MsgEnterEventHandlerP conversionToCriteriaHandler, MsgEnterEventHandlerP findOneHandler, MsgEnterEventHandlerP endHandler) {
        Objects.requireNonNull(startHandler);
        Objects.requireNonNull(authorizationHandler);
        Objects.requireNonNull(conversionToCriteriaHandler);
        Objects.requireNonNull(findOneHandler);
        Objects.requireNonNull(endHandler);
        this.startHandler = startHandler;
        this.authorizationHandler = authorizationHandler;
        this.conversionToCriteriaHandler = conversionToCriteriaHandler;
        this.findOneHandler = findOneHandler;
        this.endHandler = endHandler;
    }

    @Override
    public Flow build() {
        return Flow.builder()
            .when(States.start, Flow.on(Events.next, States.authorization))
            .when(
                States.authorization,
                Flow.on(Events.next, States.conversionToCriteria)
            )
            .when(States.conversionToCriteria, Flow.on(Events.next, States.findOne))
            .when(States.findOne, Flow.on(Events.next, States.end))
            .when(States.end, Flow.end())
            .handlersP(States.start, startHandler)
            .handlersP(States.authorization, authorizationHandler)
            .handlersP(States.conversionToCriteria, conversionToCriteriaHandler)
            .handlersP(States.findOne, findOneHandler)
            .handlersP(States.end, endHandler)
            .build();
    }
}
