package elasta.composer.message.handlers.builder.impl;

import elasta.authorization.Authorizer;
import elasta.composer.ConvertersMap;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.converter.FlowToJsonObjectMessageHandlerConverter;
import elasta.composer.flow.builder.impl.InsertFlowBuilderImpl;
import elasta.composer.message.handlers.JsonObjectMessageHandler;
import elasta.composer.message.handlers.builder.InsertMessageHandlerBuilder;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.model.response.builder.ValidationErrorModelBuilder;
import elasta.composer.producer.IdGenerator;
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
final public class InsertMessageHandlerBuilderImpl implements InsertMessageHandlerBuilder {
    final Authorizer authorizer;
    final ConvertersMap convertersMap;
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
    final FlowToJsonObjectMessageHandlerConverter flowToJsonObjectMessageHandlerConverter;

    public InsertMessageHandlerBuilderImpl(Authorizer authorizer, ConvertersMap convertersMap, String action, AuthorizationErrorModelBuilder authorizationErrorModelBuilder, String entity, String primaryKey, IdGenerator idGenerator, JsonObjectValidatorAsync jsonObjectValidatorAsync, ValidationErrorModelBuilder validationErrorModelBuilder, Orm orm, SimpleEventBus simpleEventBus, String broadcastAddress, ResponseGenerator responseGenerator, FlowToJsonObjectMessageHandlerConverter flowToJsonObjectMessageHandlerConverter) {
        Objects.requireNonNull(authorizer);
        Objects.requireNonNull(convertersMap);
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
        Objects.requireNonNull(flowToJsonObjectMessageHandlerConverter);
        this.authorizer = authorizer;
        this.convertersMap = convertersMap;
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
        this.flowToJsonObjectMessageHandlerConverter = flowToJsonObjectMessageHandlerConverter;
    }

    @Override
    public JsonObjectMessageHandler build() {

        Flow flow = new InsertFlowBuilderImpl(
            startHandler(),
            authorizeHandler(),
            idGenerationHandler(),
            validateHandler(),
            insertHandler(),
            broadcastHandler(),
            generateResponseHandler(),
            endHandler()
        ).build();

        return flowToJsonObjectMessageHandlerConverter.convert(flow);
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

    private MsgEnterEventHandlerP insertHandler() {
        return new InsertStateHandlerBuilderImpl(
            entity,
            orm
        ).build();
    }

    private MsgEnterEventHandlerP validateHandler() {
        return new ValidateStateHandlerBuilderImpl(
            entity,
            jsonObjectValidatorAsync,
            validationErrorModelBuilder
        ).build();
    }

    private MsgEnterEventHandlerP idGenerationHandler() {
        return new IdGenerationStateHandlerBuilderImpl(
            entity,
            primaryKey,
            idGenerator
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
