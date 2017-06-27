package elasta.composer.message.handlers.builder.impl;

import elasta.authorization.Authorizer;
import elasta.composer.ConvertersMap;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.converter.FlowToMessageHandlerConverter;
import elasta.composer.flow.builder.impl.AddAllFlowBuilderImpl;
import elasta.composer.message.handlers.MessageHandler;
import elasta.composer.message.handlers.builder.InsertAllMessageHandlerBuilder;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.model.response.builder.AuthorizationSuccessModelBuilder;
import elasta.composer.model.response.builder.ValidationErrorModelBuilder;
import elasta.composer.model.response.builder.ValidationSuccessModelBuilder;
import elasta.composer.state.handlers.impl.*;
import elasta.composer.state.handlers.response.generator.ResponseGenerator;
import elasta.core.flow.Flow;
import elasta.eventbus.SimpleEventBus;
import elasta.orm.Orm;
import elasta.pipeline.validator.JsonObjectValidatorAsync;
import elasta.sql.SqlDB;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 5/21/2017.
 */
final public class InsertAllMessageHandlerBuilderImpl implements InsertAllMessageHandlerBuilder {
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
    final SimpleEventBus simpleEventBus;
    final String broadcastAddress;
    final ResponseGenerator responseGenerator;
    final FlowToMessageHandlerConverter flowToMessageHandlerConverter;
    final SqlDB sqlDB;

    public InsertAllMessageHandlerBuilderImpl(Authorizer authorizer, ConvertersMap convertersMap, String action, AuthorizationErrorModelBuilder authorizationErrorModelBuilder, AuthorizationSuccessModelBuilder authorizationSuccessModelBuilder, String entity, String primaryKey, JsonObjectValidatorAsync jsonObjectValidatorAsync, ValidationErrorModelBuilder validationErrorModelBuilder, ValidationSuccessModelBuilder validationSuccessModelBuilder, Orm orm, SimpleEventBus simpleEventBus, String broadcastAddress, ResponseGenerator responseGenerator, FlowToMessageHandlerConverter flowToMessageHandlerConverter, SqlDB sqlDB) {
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
        Objects.requireNonNull(simpleEventBus);
        Objects.requireNonNull(broadcastAddress);
        Objects.requireNonNull(responseGenerator);
        Objects.requireNonNull(flowToMessageHandlerConverter);
        Objects.requireNonNull(sqlDB);
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
        this.simpleEventBus = simpleEventBus;
        this.broadcastAddress = broadcastAddress;
        this.responseGenerator = responseGenerator;
        this.flowToMessageHandlerConverter = flowToMessageHandlerConverter;
        this.sqlDB = sqlDB;
    }

    @Override
    public MessageHandler<List<JsonObject>> build() {

        Flow flow = new AddAllFlowBuilderImpl(
            startHandler(),
            authorizeAllHandler(),
            validateAllHandler(),
            insertAllHandler(),
            broadcastAllHandler(),
            generateResponseHandler(),
            endHandler()
        ).build();

        return flowToMessageHandlerConverter.convert(flow);
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

    private MsgEnterEventHandlerP insertAllHandler() {
        return new InsertAllStateHandlerBuilderImpl(
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
