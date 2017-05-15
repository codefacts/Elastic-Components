package elasta.composer.message.handlers.builder.impl;

import elasta.authorization.Authorizer;
import elasta.composer.ConvertersMap;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.flow.builder.impl.UpdateFlowBuilderImpl;
import elasta.composer.message.handlers.JsonObjectMessageHandler;
import elasta.composer.message.handlers.builder.UpdateMessageHandlerBuilder;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.model.response.builder.ValidationErrorModelBuilder;
import elasta.composer.state.handlers.UserIdConverter;
import elasta.composer.state.handlers.impl.*;
import elasta.composer.state.handlers.response.generator.ResponseGenerator;
import elasta.core.flow.Flow;
import elasta.eventbus.SimpleEventBus;
import elasta.orm.Orm;
import elasta.pipeline.validator.JsonObjectValidatorAsync;

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
    final String action;
    final AuthorizationErrorModelBuilder authorizationErrorModelBuilder;

    public UpdateMessageHandlerBuilderImpl(ResponseGenerator responseGenerator, String broadcastAddress, SimpleEventBus simpleEventBus, String entity, Orm orm, JsonObjectValidatorAsync jsonObjectValidatorAsync, ValidationErrorModelBuilder validationErrorModelBuilder, ConvertersMap convertersMap, Authorizer authorizer, String action, AuthorizationErrorModelBuilder authorizationErrorModelBuilder) {
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
        this.action = action;
        this.authorizationErrorModelBuilder = authorizationErrorModelBuilder;
    }

    @Override
    public JsonObjectMessageHandler build() {

        Flow flow = new UpdateFlowBuilderImpl(
            startHandler(),
            authorizationHandler(),
            validationHandler(),
            updateHandler(),
            broadcastHandler(),
            generateResponseHandler(),
            endHandler()
        ).build();

        return message -> flow.start(message.body());
    }

    private MsgEnterEventHandlerP endHandler() {
        return new EndStateHandlerBuilderImpl().build();
    }

    private MsgEnterEventHandlerP generateResponseHandler() {
        return new GenerateResponseStateHandlerImpl(
            responseGenerator
        ).build();
    }

    private MsgEnterEventHandlerP broadcastHandler() {
        return new BroadcastStateHandlerBuilderImpl(
            simpleEventBus,
            broadcastAddress
        ).build();
    }

    private MsgEnterEventHandlerP updateHandler() {
        return new UpdateStateHandlerBuilderImpl(
            entity,
            orm
        ).build();
    }

    private MsgEnterEventHandlerP validationHandler() {
        return new ValidationStateHandlerBuilderImpl(
            entity,
            jsonObjectValidatorAsync,
            validationErrorModelBuilder
        ).build();
    }

    private MsgEnterEventHandlerP authorizationHandler() {
        return new AuthorizationStateHandlerBuilderImpl(
            authorizer,
            action,
            authorizationErrorModelBuilder
        ).build();
    }

    private MsgEnterEventHandlerP startHandler() {
        return new StartStateHandlerBuilderImpl().build();
    }
}
