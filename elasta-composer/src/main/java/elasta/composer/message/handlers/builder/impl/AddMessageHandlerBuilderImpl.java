package elasta.composer.message.handlers.builder.impl;

import elasta.authorization.Authorizer;
import elasta.composer.ConvertersMap;
import elasta.composer.MessageBus;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.converter.FlowToJsonObjectMessageHandlerConverter;
import elasta.composer.flow.builder.impl.AddFlowBuilderImpl;
import elasta.composer.message.handlers.JsonObjectMessageHandler;
import elasta.composer.message.handlers.builder.AddMessageHandlerBuilder;
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
final public class AddMessageHandlerBuilderImpl implements AddMessageHandlerBuilder {
    final Authorizer authorizer;
    final ConvertersMap convertersMap;
    final String action;
    final AuthorizationErrorModelBuilder authorizationErrorModelBuilder;
    final String entity;
    final String primaryKey;
    final JsonObjectValidatorAsync jsonObjectValidatorAsync;
    final ValidationErrorModelBuilder validationErrorModelBuilder;
    final Orm orm;
    final MessageBus messageBus;
    final String broadcastAddress;
    final ResponseGenerator responseGenerator;
    final FlowToJsonObjectMessageHandlerConverter flowToJsonObjectMessageHandlerConverter;
    final ObjectIdGenerator objectIdGenerator;
    final SqlDB sqlDB;

    public AddMessageHandlerBuilderImpl(Authorizer authorizer, ConvertersMap convertersMap, String action, AuthorizationErrorModelBuilder authorizationErrorModelBuilder, String entity, String primaryKey, JsonObjectValidatorAsync jsonObjectValidatorAsync, ValidationErrorModelBuilder validationErrorModelBuilder, Orm orm, MessageBus messageBus, String broadcastAddress, ResponseGenerator responseGenerator, FlowToJsonObjectMessageHandlerConverter flowToJsonObjectMessageHandlerConverter, ObjectIdGenerator objectIdGenerator, SqlDB sqlDB) {
        Objects.requireNonNull(authorizer);
        Objects.requireNonNull(convertersMap);
        Objects.requireNonNull(action);
        Objects.requireNonNull(authorizationErrorModelBuilder);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(primaryKey);
        Objects.requireNonNull(jsonObjectValidatorAsync);
        Objects.requireNonNull(validationErrorModelBuilder);
        Objects.requireNonNull(orm);
        Objects.requireNonNull(messageBus);
        Objects.requireNonNull(broadcastAddress);
        Objects.requireNonNull(responseGenerator);
        Objects.requireNonNull(flowToJsonObjectMessageHandlerConverter);
        Objects.requireNonNull(objectIdGenerator);
        Objects.requireNonNull(sqlDB);
        this.authorizer = authorizer;
        this.convertersMap = convertersMap;
        this.action = action;
        this.authorizationErrorModelBuilder = authorizationErrorModelBuilder;
        this.entity = entity;
        this.primaryKey = primaryKey;
        this.jsonObjectValidatorAsync = jsonObjectValidatorAsync;
        this.validationErrorModelBuilder = validationErrorModelBuilder;
        this.orm = orm;
        this.messageBus = messageBus;
        this.broadcastAddress = broadcastAddress;
        this.responseGenerator = responseGenerator;
        this.flowToJsonObjectMessageHandlerConverter = flowToJsonObjectMessageHandlerConverter;
        this.objectIdGenerator = objectIdGenerator;
        this.sqlDB = sqlDB;
    }

    @Override
    public JsonObjectMessageHandler build() {

        Flow flow = new AddFlowBuilderImpl(
            startHandler(),
            authorizeHandler(),
            generateIdHandler(),
            validateHandler(),
            insertHandler(),
            broadcastHandler(),
            generateResponseHandler(),
            endHandler()
        ).build();

        return flowToJsonObjectMessageHandlerConverter.convert(flow);
    }

    private MsgEnterEventHandlerP generateIdHandler() {
        return new GenerateIdStateHandlerBuilderImpl(
            entity,
            objectIdGenerator
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

    private MsgEnterEventHandlerP insertHandler() {
        return new AddStateHandlerBuilderImpl(
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
