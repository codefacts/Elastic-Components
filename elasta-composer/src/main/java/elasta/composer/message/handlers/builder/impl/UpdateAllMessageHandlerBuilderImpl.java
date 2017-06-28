package elasta.composer.message.handlers.builder.impl;

import elasta.authorization.Authorizer;
import elasta.composer.ConvertersMap;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.converter.FlowToMessageHandlerConverter;
import elasta.composer.flow.builder.impl.UpdateFlowBuilderImpl;
import elasta.composer.message.handlers.MessageHandler;
import elasta.composer.message.handlers.builder.UpdateAllMessageHandlerBuilder;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.model.response.builder.AuthorizationSuccessModelBuilder;
import elasta.composer.model.response.builder.ValidationErrorModelBuilder;
import elasta.composer.model.response.builder.ValidationSuccessModelBuilder;
import elasta.composer.state.handlers.impl.*;
import elasta.composer.state.handlers.response.generator.ResponseGenerator;
import elasta.core.flow.Flow;
import elasta.eventbus.SimpleEventBus;
import elasta.orm.Orm;
import elasta.orm.idgenerator.ObjectIdGenerator;
import elasta.pipeline.validator.JsonObjectValidatorAsync;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 5/21/2017.
 */
final public class UpdateAllMessageHandlerBuilderImpl implements UpdateAllMessageHandlerBuilder {
    final ResponseGenerator responseGenerator;
    final String broadcastAddress;
    final SimpleEventBus simpleEventBus;
    final String entity;
    final Orm orm;
    final JsonObjectValidatorAsync jsonObjectValidatorAsync;
    final ValidationErrorModelBuilder validationErrorModelBuilder;
    final ValidationSuccessModelBuilder validationSuccessModelBuilder;
    final ConvertersMap convertersMap;
    final Authorizer authorizer;
    final String action;
    final AuthorizationErrorModelBuilder authorizationErrorModelBuilder;
    final AuthorizationSuccessModelBuilder authorizationSuccessModelBuilder;
    final FlowToMessageHandlerConverter flowToMessageHandlerConverter;
    final ObjectIdGenerator<Object> objectIdGenerator;

    public UpdateAllMessageHandlerBuilderImpl(ResponseGenerator responseGenerator, String broadcastAddress, SimpleEventBus simpleEventBus, String entity, Orm orm, JsonObjectValidatorAsync jsonObjectValidatorAsync, ValidationErrorModelBuilder validationErrorModelBuilder, ValidationSuccessModelBuilder validationSuccessModelBuilder, ConvertersMap convertersMap, Authorizer authorizer, String action, AuthorizationErrorModelBuilder authorizationErrorModelBuilder, AuthorizationSuccessModelBuilder authorizationSuccessModelBuilder, FlowToMessageHandlerConverter flowToMessageHandlerConverter, ObjectIdGenerator<Object> objectIdGenerator) {
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
        Objects.requireNonNull(flowToMessageHandlerConverter);
        Objects.requireNonNull(authorizationSuccessModelBuilder);
        Objects.requireNonNull(validationSuccessModelBuilder);
        Objects.requireNonNull(objectIdGenerator);
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
        this.flowToMessageHandlerConverter = flowToMessageHandlerConverter;
        this.validationSuccessModelBuilder = validationSuccessModelBuilder;
        this.authorizationSuccessModelBuilder = authorizationSuccessModelBuilder;
        this.objectIdGenerator = objectIdGenerator;
    }

    @Override
    public MessageHandler<List<JsonObject>> build() {

        Flow flow = new UpdateFlowBuilderImpl(
            startHandler(),
            authorizeAllHandler(),
            generateIdsAllHandler(),
            validateAllHandler(),
            updateAllHandler(),
            broadcastAllHandler(),
            generateResponseHandler(),
            endHandler()
        ).build();

        return flowToMessageHandlerConverter.convert(flow);
    }

    private MsgEnterEventHandlerP generateIdsAllHandler() {
        return new GenerateIdsAllStateHandlerBuilderImpl(
            entity, objectIdGenerator
        ).build();
    }

    private MsgEnterEventHandlerP endHandler() {
        return new EndStateHandlerBuilderImpl().build();
    }

    private MsgEnterEventHandlerP generateResponseHandler() {
        return new GenerateResponseStateHandlerImpl(
            responseGenerator
        ).build();
    }

    private MsgEnterEventHandlerP broadcastAllHandler() {
        return new BroadcastAllStateHandlerBuilderImpl(
            simpleEventBus,
            broadcastAddress
        ).build();
    }

    private MsgEnterEventHandlerP updateAllHandler() {
        return new UpdateAllStateHandlerBuilderImpl(
            entity,
            orm
        ).build();
    }

    private MsgEnterEventHandlerP validateAllHandler() {
        return new ValidateAllStateHandlerBuilderImpl(
            entity,
            jsonObjectValidatorAsync,
            validationErrorModelBuilder,
            validationSuccessModelBuilder
        ).build();
    }

    private MsgEnterEventHandlerP authorizeAllHandler() {
        return new AuthorizeAllStateHandlerBuilderImpl(
            authorizer,
            action,
            authorizationErrorModelBuilder,
            authorizationSuccessModelBuilder
        ).build();
    }

    private MsgEnterEventHandlerP startHandler() {
        return new StartStateHandlerBuilderImpl().build();
    }
}
