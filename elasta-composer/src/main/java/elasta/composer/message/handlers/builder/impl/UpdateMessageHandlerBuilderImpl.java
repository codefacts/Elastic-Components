package elasta.composer.message.handlers.builder.impl;

import elasta.authorization.Authorizer;
import elasta.composer.ConvertersMap;
import elasta.composer.RequestContext;
import elasta.composer.flow.builder.impl.UpdateFlowBuilderImpl;
import elasta.composer.impl.ContextHolderImpl;
import elasta.composer.impl.RequestContextImpl;
import elasta.composer.message.handlers.JsonObjectMessageHandler;
import elasta.composer.message.handlers.builder.UpdateMessageHandlerBuilder;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.model.response.builder.ValidationErrorModelBuilder;
import elasta.composer.state.handlers.UserIdConverter;
import elasta.composer.state.handlers.impl.*;
import elasta.composer.state.handlers.response.generator.ResponseGenerator;
import elasta.core.flow.EnterEventHandlerP;
import elasta.core.flow.Flow;
import elasta.eventbus.SimpleEventBus;
import elasta.orm.Orm;
import elasta.pipeline.validator.JsonObjectValidatorAsync;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 5/14/2017.
 */
final public class UpdateMessageHandlerBuilderImpl implements UpdateMessageHandlerBuilder {
    final ResponseGenerator responseGenerator;
    final String broadcastAddress;
    final SimpleEventBus simpleEventBus;
    final String entity;
    final Orm orm;
    final JsonObjectValidatorAsync jsonObjectValidatorAsync;
    final ValidationErrorModelBuilder validationErrorModelBuilder;
    final ConvertersMap convertersMap;
    final Authorizer authorizer;
    final UserIdConverter userIdConverter;
    final String action;
    final AuthorizationErrorModelBuilder authorizationErrorModelBuilder;

    public UpdateMessageHandlerBuilderImpl(ResponseGenerator responseGenerator, String broadcastAddress, SimpleEventBus simpleEventBus, String entity, Orm orm, JsonObjectValidatorAsync jsonObjectValidatorAsync, ValidationErrorModelBuilder validationErrorModelBuilder, ConvertersMap convertersMap, Authorizer authorizer, UserIdConverter userIdConverter, String action, AuthorizationErrorModelBuilder authorizationErrorModelBuilder) {
        Objects.requireNonNull(responseGenerator);
        Objects.requireNonNull(broadcastAddress);
        Objects.requireNonNull(simpleEventBus);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(orm);
        Objects.requireNonNull(jsonObjectValidatorAsync);
        Objects.requireNonNull(validationErrorModelBuilder);
        Objects.requireNonNull(convertersMap);
        Objects.requireNonNull(action);
        Objects.requireNonNull(authorizationErrorModelBuilder);
        this.responseGenerator = responseGenerator;
        this.broadcastAddress = broadcastAddress;
        this.simpleEventBus = simpleEventBus;
        this.entity = entity;
        this.orm = orm;
        this.jsonObjectValidatorAsync = jsonObjectValidatorAsync;
        this.validationErrorModelBuilder = validationErrorModelBuilder;
        this.convertersMap = convertersMap;
        this.authorizer = authorizer;
        this.userIdConverter = userIdConverter;
        this.action = action;
        this.authorizationErrorModelBuilder = authorizationErrorModelBuilder;
    }

    @Override
    public JsonObjectMessageHandler build() {

        final ContextHolderImpl contextHolder = new ContextHolderImpl();
        final RequestContextImpl requestContext = new RequestContextImpl(
            contextHolder,
            convertersMap.getMap()
        );

        Flow flow = new UpdateFlowBuilderImpl(
            startHandler(),
            authorizationHandler(requestContext),
            validationHandler(),
            updateHandler(),
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

    private EnterEventHandlerP updateHandler() {
        return new UpdateStateHandlerBuilderImpl(
            entity,
            orm
        ).build();
    }

    private EnterEventHandlerP validationHandler() {
        return new ValidationStateHandlerBuilderImpl(
            entity,
            jsonObjectValidatorAsync,
            validationErrorModelBuilder
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
