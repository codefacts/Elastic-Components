package tracker.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import elasta.composer.MessageBus;
import elasta.composer.MessageProcessingErrorHandler;
import elasta.composer.States;
import elasta.composer.converter.FlowToJsonObjectMessageHandlerConverter;
import elasta.composer.flow.holder.FindAllFlowHolder;
import elasta.composer.message.handlers.builder.impl.FindAllMessageHandlerBuilderImpl;
import elasta.core.flow.Flow;
import elasta.module.ModuleSystem;
import elasta.module.ModuleSystemBuilder;
import elasta.orm.Orm;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import elasta.sql.SqlExecutor;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import tracker.*;
import tracker.entity_config.Entities;
import tracker.message.handlers.impl.AuthenticateMessageHandlerImpl;
import tracker.message.handlers.impl.DelegatingMessageHandlerImpl;
import tracker.model.AuthRequestModel;
import tracker.model.BaseModel;
import tracker.model.PositionModel;
import tracker.model.UserModel;
import tracker.state.handlers.FindAllLatestPositionsGroupByUserId;

import java.util.Objects;

/**
 * Created by sohan on 6/25/2017.
 */
final public class AppImpl implements App {
    final ModuleSystem module;
    final EventBus eventBus;

    public AppImpl(Config config) {

        Objects.requireNonNull(config);

        final ModuleSystemBuilder builder = ModuleSystem.builder();

        TrackerExporter.exportTo(
            TrackerExporter.ExportToParams.builder()
                .builder(builder)
                .config(config)
                .build()
        );

        module = builder.build();
        eventBus = module.require(EventBus.class);

        createMessageHandlers(module);

        MessageBus messageBus = module.require(MessageBus.class);
    }

    private ModuleSystem createMessageHandlers(ModuleSystem module) {

        module.require(MessageHandlersBuilder.class).build(
            MessageHandlersBuilder.BuildParams.builder()
                .module(module)
                .flowParamss(ImmutableList.of(
                    new MessageHandlersBuilder.FlowParams(
                        Entities.USER_ENTITY,
                        new FieldExpressionImpl(
                            "r." + BaseModel.id
                        )
                    ),
                    new MessageHandlersBuilder.FlowParams(
                        Entities.DEVICE_ENTITY,
                        new FieldExpressionImpl(
                            "r." + BaseModel.id
                        )
                    ),
                    new MessageHandlersBuilder.FlowParams(
                        Entities.POSITION_ENTITY,
                        new FieldExpressionImpl(
                            "r." + BaseModel.id
                        )
                    ),
                    new MessageHandlersBuilder.FlowParams(
                        Entities.OUTLET_ENTITY,
                        new FieldExpressionImpl(
                            "r." + BaseModel.id
                        )
                    )
                ))
                .build()
        ).forEach(this::addMessageHandler);

        addMessageHandler(
            createAuthMessageHandlers(module)
        );

        addMessageHandler(
            createFindAllPositionsGroupByUserId()
        );

        return module;
    }

    private MessageHandlersBuilder.AddressAndHandler createFindAllPositionsGroupByUserId() {

        return new MessageHandlersBuilder.AddressAndHandler(
            Addresses.findAllPositionsGroupByUserId,
            new FindAllMessageHandlerBuilderImpl(
                new FindAllFlowHolder(
                    Flow.builder(
                        module.require(FlowBuilderHelper.class).findAllFlowHolder(
                            FlowBuilderHelper.FindAllParams.builder()
                                .module(module)
                                .entity(Entities.POSITION_ENTITY)
                                .action(Addresses.findAllPositionsGroupByUserId)
                                .paginationKey(
                                    new FieldExpressionImpl(
                                        "r." + PositionModel.id
                                    )
                                )
                                .build()
                        ).getFlow()
                    ).handlersP(States.findAll, new FindAllLatestPositionsGroupByUserId(module.require(SqlExecutor.class))).build()
                ),
                module.require(FlowToJsonObjectMessageHandlerConverter.class)
            ).build()
        );
    }

    private void addMessageHandler(MessageHandlersBuilder.AddressAndHandler addressAndHandler) {
        eventBus.consumer(
            addressAndHandler.getAddress(),
            new DelegatingMessageHandlerImpl(
                addressAndHandler.getMessageHandler(),
                module.require(MessageProcessingErrorHandler.class)
            )
        );
    }

    private MessageHandlersBuilder.AddressAndHandler createAuthMessageHandlers(ModuleSystem module) {

        return new MessageHandlersBuilder.AddressAndHandler(
            Addresses.authenticate,
            new AuthenticateMessageHandlerImpl(module.require(Orm.class), ImmutableSet.of(
                UserModel.userId,
                UserModel.username,
                UserModel.email,
                UserModel.phone
            ), AuthRequestModel.user, AuthRequestModel.password)
        );
    }

    @Override
    public App close() {
        return this;
    }

    @Override
    public MessageBus mesageBus() {
        return module.require(MessageBus.class);
    }

    public static void main(String[] asfd) {
        new AppImpl(
            new Config(
                new JsonObject(
                    ImmutableMap.of(
                        "user", "root",
                        "password", "",
                        "driver_class", "com.mysql.jdbc.Driver",
                        "url", "jdbc:mysql://localhost/tracker_db"
                    )
                ),
                ImmutableMap.of(),
                Vertx.vertx(),
                1,
                10,
                "r",
                "kdheofdsys;fhrvtwo38rpcmbgbhdiig-b7wngy9gir993,vh9dte-46to3nf8gyd",
                12
            )
        );
    }
}
