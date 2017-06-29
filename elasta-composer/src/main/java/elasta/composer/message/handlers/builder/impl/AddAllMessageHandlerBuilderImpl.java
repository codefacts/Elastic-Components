package elasta.composer.message.handlers.builder.impl;

import elasta.authorization.Authorizer;
import elasta.composer.ConvertersMap;
import elasta.composer.MessageBus;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.converter.FlowToJsonArrayMessageHandlerConverter;
import elasta.composer.flow.builder.impl.AddAllFlowBuilderImpl;
import elasta.composer.message.handlers.JsonArrayMessageHandler;
import elasta.composer.message.handlers.builder.AddAllMessageHandlerBuilder;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.model.response.builder.AuthorizationSuccessModelBuilder;
import elasta.composer.model.response.builder.ValidationErrorModelBuilder;
import elasta.composer.model.response.builder.ValidationSuccessModelBuilder;
import elasta.composer.state.handlers.impl.*;
import elasta.composer.state.handlers.response.generator.ResponseGenerator;
import elasta.core.flow.Flow;
import elasta.orm.Orm;
import elasta.orm.idgenerator.ObjectIdGenerator;
import elasta.pipeline.validator.JsonObjectValidatorAsync;
import elasta.sql.SqlDB;

import java.util.Objects;

/**
 * Created by sohan on 5/21/2017.
 */
final public class AddAllMessageHandlerBuilderImpl implements AddAllMessageHandlerBuilder {
    final Authorizer authorizer;
    final ConvertersMap convertersMap;
    final String action;
    final AuthorizationErrorModelBuilder authorizationErrorModelBuilder;
    final AuthorizationSuccessModelBuilder authorizationSuccessModelBuilder;
    final String entity;
    final String primaryKey;
    final JsonObjectValidatorAsync jsonObjectValidatorAsync;
    final ValidationErrorModelBuilder validationErrorModelBuilder;
    final ValidationSuccessModelBuilder validationSuccessModelBuilder;
    final Orm orm;
    final MessageBus messageBus;
    final String broadcastAddress;
    final ResponseGenerator responseGenerator;
    final FlowToJsonArrayMessageHandlerConverter flowToJsonArrayMessageHandlerConverter;
    final SqlDB sqlDB;
    final ObjectIdGenerator<Object> objectIdGenerator;

    public AddAllMessageHandlerBuilderImpl(Authorizer authorizer, ConvertersMap convertersMap, String action, AuthorizationErrorModelBuilder authorizationErrorModelBuilder, AuthorizationSuccessModelBuilder authorizationSuccessModelBuilder, String entity, String primaryKey, JsonObjectValidatorAsync jsonObjectValidatorAsync, ValidationErrorModelBuilder validationErrorModelBuilder, ValidationSuccessModelBuilder validationSuccessModelBuilder, Orm orm, MessageBus messageBus, String broadcastAddress, ResponseGenerator responseGenerator, FlowToJsonArrayMessageHandlerConverter flowToJsonArrayMessageHandlerConverter, SqlDB sqlDB, ObjectIdGenerator<Object> objectIdGenerator) {
        Objects.requireNonNull(authorizer);
        Objects.requireNonNull(convertersMap);
        Objects.requireNonNull(action);
        Objects.requireNonNull(authorizationErrorModelBuilder);
        Objects.requireNonNull(authorizationSuccessModelBuilder);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(primaryKey);
        Objects.requireNonNull(jsonObjectValidatorAsync);
        Objects.requireNonNull(validationErrorModelBuilder);
        Objects.requireNonNull(validationSuccessModelBuilder);
        Objects.requireNonNull(orm);
        Objects.requireNonNull(messageBus);
        Objects.requireNonNull(broadcastAddress);
        Objects.requireNonNull(responseGenerator);
        Objects.requireNonNull(flowToJsonArrayMessageHandlerConverter);
        Objects.requireNonNull(sqlDB);
        Objects.requireNonNull(objectIdGenerator);
        this.authorizer = authorizer;
        this.convertersMap = convertersMap;
        this.action = action;
        this.authorizationErrorModelBuilder = authorizationErrorModelBuilder;
        this.authorizationSuccessModelBuilder = authorizationSuccessModelBuilder;
        this.entity = entity;
        this.primaryKey = primaryKey;
        this.jsonObjectValidatorAsync = jsonObjectValidatorAsync;
        this.validationErrorModelBuilder = validationErrorModelBuilder;
        this.validationSuccessModelBuilder = validationSuccessModelBuilder;
        this.orm = orm;
        this.messageBus = messageBus;
        this.broadcastAddress = broadcastAddress;
        this.responseGenerator = responseGenerator;
        this.flowToJsonArrayMessageHandlerConverter = flowToJsonArrayMessageHandlerConverter;
        this.sqlDB = sqlDB;
        this.objectIdGenerator = objectIdGenerator;
    }

    @Override
    public JsonArrayMessageHandler build() {

        Flow flow = new AddAllFlowBuilderImpl(
            startHandler(),
            authorizeAllHandler(),
            generateIdsAllHandler(),
            validateAllHandler(),
            insertAllHandler(),
            broadcastAllHandler(),
            generateResponseHandler(),
            endHandler()
        ).build();

        return flowToJsonArrayMessageHandlerConverter.convert(flow);
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
            messageBus,
            broadcastAddress
        ).build();
    }

    private MsgEnterEventHandlerP insertAllHandler() {
        return new AddAllStateHandlerBuilderImpl(
            entity,
            sqlDB,
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
