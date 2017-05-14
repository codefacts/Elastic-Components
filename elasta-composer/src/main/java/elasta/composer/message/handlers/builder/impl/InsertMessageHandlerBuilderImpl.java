package elasta.composer.message.handlers.builder.impl;

import elasta.authorization.Authorizer;
import elasta.composer.ContextHolder;
import elasta.composer.ConvertersMap;
import elasta.composer.RequestContext;
import elasta.composer.flow.builder.impl.InsertFlowBuilderImpl;
import elasta.composer.impl.ContextHolderImpl;
import elasta.composer.impl.RequestContextImpl;
import elasta.composer.message.handlers.JsonObjectMessageHandler;
import elasta.composer.message.handlers.builder.InsertMessageHandlerBuilder;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.model.response.builder.ValidationErrorModelBuilder;
import elasta.composer.producer.IdGenerator;
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
final public class InsertMessageHandlerBuilderImpl implements InsertMessageHandlerBuilder {
    final Authorizer authorizer;
    final ConvertersMap convertersMap;
    final UserIdConverter userIdConverter;
    final String action;
    final AuthorizationErrorModelBuilder authorizationErrorModelBuilder;
    final String entity;
    final String primaryKey;
    final IdGenerator idGenerator;
    final JsonObjectValidatorAsync jsonObjectValidatorAsync;
    final ValidationErrorModelBuilder validationErrorModelBuilder;
    final Orm orm;
    final SimpleEventBus simpleEventBus;
    final String broadcastAddress;
    final ResponseGenerator responseGenerator;

    public InsertMessageHandlerBuilderImpl(Authorizer authorizer, ConvertersMap convertersMap, UserIdConverter userIdConverter, String action, AuthorizationErrorModelBuilder authorizationErrorModelBuilder, String entity, String primaryKey, IdGenerator idGenerator, JsonObjectValidatorAsync jsonObjectValidatorAsync, ValidationErrorModelBuilder validationErrorModelBuilder, Orm orm, SimpleEventBus simpleEventBus, String broadcastAddress, ResponseGenerator responseGenerator) {
        Objects.requireNonNull(authorizer);
        Objects.requireNonNull(convertersMap);
        Objects.requireNonNull(userIdConverter);
        Objects.requireNonNull(action);
        Objects.requireNonNull(authorizationErrorModelBuilder);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(primaryKey);
        Objects.requireNonNull(idGenerator);
        Objects.requireNonNull(jsonObjectValidatorAsync);
        Objects.requireNonNull(validationErrorModelBuilder);
        Objects.requireNonNull(orm);
        Objects.requireNonNull(simpleEventBus);
        Objects.requireNonNull(broadcastAddress);
        Objects.requireNonNull(responseGenerator);
        this.authorizer = authorizer;
        this.convertersMap = convertersMap;
        this.userIdConverter = userIdConverter;
        this.action = action;
        this.authorizationErrorModelBuilder = authorizationErrorModelBuilder;
        this.entity = entity;
        this.primaryKey = primaryKey;
        this.idGenerator = idGenerator;
        this.jsonObjectValidatorAsync = jsonObjectValidatorAsync;
        this.validationErrorModelBuilder = validationErrorModelBuilder;
        this.orm = orm;
        this.simpleEventBus = simpleEventBus;
        this.broadcastAddress = broadcastAddress;
        this.responseGenerator = responseGenerator;
    }

    @Override
    public JsonObjectMessageHandler build() {

        final ContextHolderImpl contextHolder = new ContextHolderImpl();
        RequestContext requestContext = new RequestContextImpl(contextHolder, convertersMap.getMap());

        Flow flow = new InsertFlowBuilderImpl(
            startHandler(),
            authorizationHandler(requestContext),
            idGenerationHandler(),
            validationHandler(),
            insertHandler(),
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

    private EnterEventHandlerP insertHandler() {
        return new InsertStateHandlerBuilderImpl(
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

    private EnterEventHandlerP idGenerationHandler() {
        return new IdGenerationStateHandlerBuilderImpl(
            entity,
            primaryKey,
            idGenerator
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
