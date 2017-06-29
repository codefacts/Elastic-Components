package elasta.composer.message.handlers.builder.impl;

import elasta.authorization.Authorizer;
import elasta.composer.ConvertersMap;
import elasta.composer.MessageBus;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.converter.FlowToMessageHandlerConverter;
import elasta.composer.flow.builder.impl.DeleteFlowBuilderImpl;
import elasta.composer.message.handlers.MessageHandler;
import elasta.composer.message.handlers.builder.DeleteMessageHandlerBuilder;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.state.handlers.impl.*;
import elasta.composer.state.handlers.response.generator.ResponseGenerator;
import elasta.core.flow.Flow;
import elasta.orm.Orm;
import elasta.sql.SqlDB;

import java.util.Objects;

/**
 * Created by sohan on 5/14/2017.
 */
final public class DeleteMessageHandlerBuilderImpl<T> implements DeleteMessageHandlerBuilder<T> {
    final ResponseGenerator responseGenerator;
    final MessageBus messageBus;
    final String broadcastAddress;
    final Orm orm;
    final String entity;
    final String action;
    final Authorizer authorizer;
    final AuthorizationErrorModelBuilder authorizationErrorModelBuilder;
    final ConvertersMap convertersMap;
    final FlowToMessageHandlerConverter<T> flowToMessageHandlerConverter;
    final SqlDB sqlDB;

    public DeleteMessageHandlerBuilderImpl(ResponseGenerator responseGenerator, MessageBus messageBus, String broadcastAddress, Orm orm, String entity, String action, Authorizer authorizer, AuthorizationErrorModelBuilder authorizationErrorModelBuilder, ConvertersMap convertersMap, FlowToMessageHandlerConverter<T> flowToMessageHandlerConverter, SqlDB sqlDB) {
        Objects.requireNonNull(responseGenerator);
        Objects.requireNonNull(messageBus);
        Objects.requireNonNull(broadcastAddress);
        Objects.requireNonNull(orm);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(action);
        Objects.requireNonNull(authorizer);
        Objects.requireNonNull(authorizationErrorModelBuilder);
        Objects.requireNonNull(convertersMap);
        Objects.requireNonNull(flowToMessageHandlerConverter);
        Objects.requireNonNull(sqlDB);
        this.responseGenerator = responseGenerator;
        this.messageBus = messageBus;
        this.broadcastAddress = broadcastAddress;
        this.orm = orm;
        this.entity = entity;
        this.action = action;
        this.authorizer = authorizer;
        this.authorizationErrorModelBuilder = authorizationErrorModelBuilder;
        this.convertersMap = convertersMap;
        this.flowToMessageHandlerConverter = flowToMessageHandlerConverter;
        this.sqlDB = sqlDB;
    }

    @Override
    public MessageHandler<T> build() {

        Flow flow = new DeleteFlowBuilderImpl(
            startHandler(),
            authorizeHandler(),
            deleteHandler(),
            broadcastHandler(),
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

    private MsgEnterEventHandlerP broadcastHandler() {
        return new BroadcastStateHandlerBuilderImpl(
            messageBus,
            broadcastAddress
        ).build();
    }

    private MsgEnterEventHandlerP deleteHandler() {
        return new DeleteStateHandlerBuilderImpl(
            orm,
            sqlDB,
            entity
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
