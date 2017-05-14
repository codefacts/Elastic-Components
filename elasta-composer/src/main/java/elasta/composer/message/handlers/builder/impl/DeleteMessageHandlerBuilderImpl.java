package elasta.composer.message.handlers.builder.impl;

import elasta.authorization.Authorizer;
import elasta.composer.ConvertersMap;
import elasta.composer.RequestContext;
import elasta.composer.flow.builder.impl.DeleteFlowBuilderImpl;
import elasta.composer.impl.ContextHolderImpl;
import elasta.composer.impl.RequestContextImpl;
import elasta.composer.message.handlers.MessageHandler;
import elasta.composer.message.handlers.builder.DeleteMessageHandlerBuilder;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.state.handlers.UserIdConverter;
import elasta.composer.state.handlers.impl.*;
import elasta.composer.state.handlers.response.generator.ResponseGenerator;
import elasta.core.flow.EnterEventHandlerP;
import elasta.core.flow.Flow;
import elasta.eventbus.SimpleEventBus;
import elasta.orm.Orm;

import java.util.Objects;

/**
 * Created by sohan on 5/14/2017.
 */
final public class DeleteMessageHandlerBuilderImpl<T> implements DeleteMessageHandlerBuilder<T> {
    final ResponseGenerator responseGenerator;
    final SimpleEventBus simpleEventBus;
    final String broadcastAddress;
    final Orm orm;
    final String entity;
    final String action;
    final Authorizer authorizer;
    final UserIdConverter userIdConverter;
    final AuthorizationErrorModelBuilder authorizationErrorModelBuilder;
    final ConvertersMap convertersMap;

    public DeleteMessageHandlerBuilderImpl(ResponseGenerator responseGenerator, SimpleEventBus simpleEventBus, String broadcastAddress, Orm orm, String entity, String action, Authorizer authorizer, UserIdConverter userIdConverter, AuthorizationErrorModelBuilder authorizationErrorModelBuilder, ConvertersMap convertersMap) {
        Objects.requireNonNull(responseGenerator);
        Objects.requireNonNull(simpleEventBus);
        Objects.requireNonNull(broadcastAddress);
        Objects.requireNonNull(orm);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(action);
        Objects.requireNonNull(authorizer);
        Objects.requireNonNull(userIdConverter);
        Objects.requireNonNull(authorizationErrorModelBuilder);
        Objects.requireNonNull(convertersMap);
        this.responseGenerator = responseGenerator;
        this.simpleEventBus = simpleEventBus;
        this.broadcastAddress = broadcastAddress;
        this.orm = orm;
        this.entity = entity;
        this.action = action;
        this.authorizer = authorizer;
        this.userIdConverter = userIdConverter;
        this.authorizationErrorModelBuilder = authorizationErrorModelBuilder;
        this.convertersMap = convertersMap;
    }

    @Override
    public MessageHandler<T> build() {

        ContextHolderImpl contextHolder = new ContextHolderImpl();

        final RequestContext requestContext = new RequestContextImpl(contextHolder, convertersMap.getMap());

        Flow flow = new DeleteFlowBuilderImpl(
            startHandler(),
            authorizationHandler(requestContext),
            deleteHandler(),
            broadcastHandler(),
            generateResponseHandler(),
            endHandler()
        ).build();

        return message -> flow.start(message.body());
    }

    private EnterEventHandlerP endHandler() {
        return new EndStateHandlerBuilderImpl().build();
    }

    private EnterEventHandlerP generateResponseHandler() {
        return new GenerateResponseStateHandlerImpl(
            responseGenerator
        ).build();
    }

    private EnterEventHandlerP broadcastHandler() {
        return new BroadcastStateHandlerBuilderImpl(
            simpleEventBus,
            broadcastAddress
        ).build();
    }

    private EnterEventHandlerP deleteHandler() {
        return new DeleteStateHandlerBuilderImpl(
            orm,
            entity
        ).build();
    }

    private EnterEventHandlerP authorizationHandler(RequestContext requestContext) {
        return new AuthorizationStateHandlerBuilderImpl(
            authorizer,
            requestContext,
            userIdConverter,
            action,
            authorizationErrorModelBuilder
        ).build();
    }

    private EnterEventHandlerP startHandler() {
        return new StartStateHandlerBuilderImpl().build();
    }
}
