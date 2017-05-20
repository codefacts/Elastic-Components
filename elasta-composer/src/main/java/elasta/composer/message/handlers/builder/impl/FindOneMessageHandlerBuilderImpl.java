package elasta.composer.message.handlers.builder.impl;

import elasta.authorization.Authorizer;
import elasta.composer.ConvertersMap;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.converter.FlowToMessageHandlerConverter;
import elasta.composer.flow.builder.impl.FindOneFlowBuilderImpl;
import elasta.composer.message.handlers.MessageHandler;
import elasta.composer.message.handlers.builder.FindOneMessageHandlerBuilder;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.state.handlers.impl.*;
import elasta.core.flow.Flow;
import elasta.orm.Orm;
import elasta.orm.query.expression.FieldExpression;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by sohan on 5/14/2017.
 */
final public class FindOneMessageHandlerBuilderImpl implements FindOneMessageHandlerBuilder {
    final String alias;
    final String entity;
    final FieldExpression findOneByFieldExpression;
    final Collection<FieldExpression> selections;
    final Orm orm;
    final Authorizer authorizer;
    final String action;
    final AuthorizationErrorModelBuilder authorizationErrorModelBuilder;
    final ConvertersMap convertersMap;
    final FlowToMessageHandlerConverter flowToMessageHandlerConverter;

    public FindOneMessageHandlerBuilderImpl(String alias, String entity, FieldExpression findOneByFieldExpression, Collection<FieldExpression> selections, Orm orm, Authorizer authorizer, String action, AuthorizationErrorModelBuilder authorizationErrorModelBuilder, ConvertersMap convertersMap, FlowToMessageHandlerConverter flowToMessageHandlerConverter) {
        Objects.requireNonNull(alias);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(findOneByFieldExpression);
        Objects.requireNonNull(selections);
        Objects.requireNonNull(orm);
        Objects.requireNonNull(authorizer);
        Objects.requireNonNull(action);
        Objects.requireNonNull(authorizationErrorModelBuilder);
        Objects.requireNonNull(convertersMap);
        Objects.requireNonNull(flowToMessageHandlerConverter);
        this.alias = alias;
        this.entity = entity;
        this.findOneByFieldExpression = findOneByFieldExpression;
        this.selections = selections;
        this.orm = orm;
        this.authorizer = authorizer;
        this.action = action;
        this.authorizationErrorModelBuilder = authorizationErrorModelBuilder;
        this.convertersMap = convertersMap;
        this.flowToMessageHandlerConverter = flowToMessageHandlerConverter;
    }

    @Override
    public MessageHandler<Object> build() {

        Flow flow = new FindOneFlowBuilderImpl(
            startHandler(),
            authorizeHandler(),
            conversionToCriteriaHandler(),
            findOneHandler(),
            endHandler()
        ).build();

        return flowToMessageHandlerConverter.convert(flow);
    }

    private MsgEnterEventHandlerP endHandler() {
        return new EndStateHandlerBuilderImpl().build();
    }

    private MsgEnterEventHandlerP findOneHandler() {
        return new FindOneStateHandlerBuilderImpl(
            alias,
            entity,
            selections,
            orm
        ).build();
    }

    private MsgEnterEventHandlerP conversionToCriteriaHandler() {
        return new ConversionToCriteriaStateHandlerBuilderImpl(
            findOneByFieldExpression
        ).build();
    }

    private MsgEnterEventHandlerP authorizeHandler() {
        return new AuthorizeStateHandlerBuilderImpl(
            authorizer,
            action,
            authorizationErrorModelBuilder
        ).build();
    }

    private MsgEnterEventHandlerP startHandler() {
        return new StartStateHandlerBuilderImpl().build();
    }
}
