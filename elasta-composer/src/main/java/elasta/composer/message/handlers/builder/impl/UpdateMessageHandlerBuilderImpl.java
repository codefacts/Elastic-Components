package elasta.composer.message.handlers.builder.impl;

import elasta.authorization.Authorizer;
import elasta.composer.ConvertersMap;
import elasta.composer.MessageBus;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.converter.FlowToJsonObjectMessageHandlerConverter;
import elasta.composer.flow.builder.impl.UpdateFlowBuilderImpl;
import elasta.composer.message.handlers.JsonObjectMessageHandler;
import elasta.composer.message.handlers.builder.UpdateMessageHandlerBuilder;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.model.response.builder.ValidationErrorModelBuilder;
import elasta.composer.state.handlers.impl.*;
import elasta.composer.state.handlers.response.generator.ResponseGenerator;
import elasta.core.flow.Flow;
import elasta.orm.Orm;
import elasta.orm.idgenerator.ObjectIdGenerator;
import elasta.pipeline.validator.JsonObjectValidatorAsync;
import elasta.sql.SqlDB;

import java.util.Objects;

/**
 * Created by sohan on 5/14/2017.
 */
final public class UpdateMessageHandlerBuilderImpl implements UpdateMessageHandlerBuilder {
    final ResponseGenerator responseGenerator;
    final String broadcastAddress;
    final MessageBus messageBus;
    final String entity;
    final Orm orm;
    final JsonObjectValidatorAsync jsonObjectValidatorAsync;
    final ValidationErrorModelBuilder validationErrorModelBuilder;
    final ConvertersMap convertersMap;
    final Authorizer authorizer;
    final String action;
    final AuthorizationErrorModelBuilder authorizationErrorModelBuilder;
    final FlowToJsonObjectMessageHandlerConverter flowToJsonObjectMessageHandlerConverter;
    final ObjectIdGenerator objectIdGenerator;
    final SqlDB sqlDB;

    public UpdateMessageHandlerBuilderImpl(ResponseGenerator responseGenerator, String broadcastAddress, MessageBus messageBus, String entity, Orm orm, JsonObjectValidatorAsync jsonObjectValidatorAsync, ValidationErrorModelBuilder validationErrorModelBuilder, ConvertersMap convertersMap, Authorizer authorizer, String action, AuthorizationErrorModelBuilder authorizationErrorModelBuilder, FlowToJsonObjectMessageHandlerConverter flowToJsonObjectMessageHandlerConverter, ObjectIdGenerator objectIdGenerator, SqlDB sqlDB) {
        Objects.requireNonNull(responseGenerator);
        Objects.requireNonNull(broadcastAddress);
        Objects.requireNonNull(messageBus);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(orm);
        Objects.requireNonNull(jsonObjectValidatorAsync);
        Objects.requireNonNull(validationErrorModelBuilder);
        Objects.requireNonNull(convertersMap);
        Objects.requireNonNull(action);
        Objects.requireNonNull(authorizationErrorModelBuilder);
        Objects.requireNonNull(flowToJsonObjectMessageHandlerConverter);
        Objects.requireNonNull(objectIdGenerator);
        Objects.requireNonNull(sqlDB);
        this.responseGenerator = responseGenerator;
        this.broadcastAddress = broadcastAddress;
        this.messageBus = messageBus;
        this.entity = entity;
        this.orm = orm;
        this.jsonObjectValidatorAsync = jsonObjectValidatorAsync;
        this.validationErrorModelBuilder = validationErrorModelBuilder;
        this.convertersMap = convertersMap;
        this.authorizer = authorizer;
        this.action = action;
        this.authorizationErrorModelBuilder = authorizationErrorModelBuilder;
        this.flowToJsonObjectMessageHandlerConverter = flowToJsonObjectMessageHandlerConverter;
        this.objectIdGenerator = objectIdGenerator;
        this.sqlDB = sqlDB;
    }

    @Override
    public JsonObjectMessageHandler build() {

        Flow flow = new UpdateFlowBuilderImpl(
            startHandler(),
            authorizeHandler(),
            generateIdHandler(),
            validateHandler(),
            updateHandler(),
            broadcastHandler(),
            generateResponseHandler(),
            endHandler()
        ).build();

        return flowToJsonObjectMessageHandlerConverter.convert(flow);
    }

    private MsgEnterEventHandlerP generateIdHandler() {
        return new GenerateIdStateHandlerBuilderImpl(
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

    private MsgEnterEventHandlerP broadcastHandler() {
        return new BroadcastStateHandlerBuilderImpl(
            messageBus,
            broadcastAddress
        ).build();
    }

    private MsgEnterEventHandlerP updateHandler() {
        return new UpdateStateHandlerBuilderImpl(
            entity,
            orm,
            sqlDB
        ).build();
    }

    private MsgEnterEventHandlerP validateHandler() {
        return new ValidateStateHandlerBuilderImpl(
            entity,
            jsonObjectValidatorAsync,
            validationErrorModelBuilder
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
