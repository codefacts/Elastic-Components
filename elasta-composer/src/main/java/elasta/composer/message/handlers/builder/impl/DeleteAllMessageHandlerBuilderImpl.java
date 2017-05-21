package elasta.composer.message.handlers.builder.impl;

import elasta.authorization.Authorizer;
import elasta.composer.ConvertersMap;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.converter.FlowToMessageHandlerConverter;
import elasta.composer.converter.FlowToJsonArrayMessageHandlerConverter;
import elasta.composer.flow.builder.impl.DeleteAllFlowBuilderImpl;
import elasta.composer.message.handlers.MessageHandler;
import elasta.composer.message.handlers.builder.DeleteAllMessageHandlerBuilder;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.model.response.builder.AuthorizationSuccessModelBuilder;
import elasta.composer.state.handlers.impl.*;
import elasta.composer.state.handlers.response.generator.ResponseGenerator;
import elasta.core.flow.Flow;
import elasta.eventbus.SimpleEventBus;
import elasta.orm.Orm;
import io.vertx.core.json.JsonArray;

import java.util.Objects;

/**
 * Created by sohan on 5/21/2017.
 */
final public class DeleteAllMessageHandlerBuilderImpl implements DeleteAllMessageHandlerBuilder {
    final ResponseGenerator responseGenerator;
    final SimpleEventBus simpleEventBus;
    final String broadcastAddress;
    final Orm orm;
    final String entity;
    final String action;
    final Authorizer authorizer;
    final AuthorizationErrorModelBuilder authorizationErrorModelBuilder;
    final AuthorizationSuccessModelBuilder authorizationSuccessModelBuilder;
    final ConvertersMap convertersMap;
    final FlowToJsonArrayMessageHandlerConverter flowToJsonArrayMessageHandlerConverter;

    public DeleteAllMessageHandlerBuilderImpl(ResponseGenerator responseGenerator, SimpleEventBus simpleEventBus, String broadcastAddress, Orm orm, String entity, String action, Authorizer authorizer, AuthorizationErrorModelBuilder authorizationErrorModelBuilder, AuthorizationSuccessModelBuilder authorizationSuccessModelBuilder, ConvertersMap convertersMap, FlowToJsonArrayMessageHandlerConverter flowToJsonArrayMessageHandlerConverter) {
        Objects.requireNonNull(responseGenerator);
        Objects.requireNonNull(simpleEventBus);
        Objects.requireNonNull(broadcastAddress);
        Objects.requireNonNull(orm);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(action);
        Objects.requireNonNull(authorizer);
        Objects.requireNonNull(authorizationErrorModelBuilder);
        Objects.requireNonNull(authorizationSuccessModelBuilder);
        Objects.requireNonNull(convertersMap);
        Objects.requireNonNull(flowToJsonArrayMessageHandlerConverter);
        this.responseGenerator = responseGenerator;
        this.simpleEventBus = simpleEventBus;
        this.broadcastAddress = broadcastAddress;
        this.orm = orm;
        this.entity = entity;
        this.action = action;
        this.authorizer = authorizer;
        this.authorizationErrorModelBuilder = authorizationErrorModelBuilder;
        this.authorizationSuccessModelBuilder = authorizationSuccessModelBuilder;
        this.convertersMap = convertersMap;
        this.flowToJsonArrayMessageHandlerConverter = flowToJsonArrayMessageHandlerConverter;
    }

    @Override
    public MessageHandler<JsonArray> build() {

        Flow flow = new DeleteAllFlowBuilderImpl(
            startHandler(),
            authorizeAllHandler(),
            deleteAllHandler(),
            broadcastAllHandler(),
            generateResponseHandler(),
            endHandler()
        ).build();

        return flowToJsonArrayMessageHandlerConverter.convert(flow);
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

    private MsgEnterEventHandlerP deleteAllHandler() {
        return new DeleteAllStateHandlerBuilderImpl(
            orm,
            entity
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
