package tracker.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.MessageBus;
import elasta.composer.MessageProcessingErrorHandler;
import elasta.module.ModuleSystem;
import elasta.module.ModuleSystemBuilder;
import elasta.orm.Orm;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import tracker.*;
import tracker.entity_config.Entities;
import tracker.message.handlers.impl.AuthenticateMessageHandlerImpl;
import tracker.message.handlers.impl.DelegatingMessageHandlerImpl;
import tracker.model.UserModel;

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
                        Entities.USER,
                        new FieldExpressionImpl(
                            "r." + UserModel.id
                        )
                    )
                ))
                .build()
        ).forEach(this::addMessageHandler);

        addMessageHandler(
            createAuthMessageHandlers(module)
        );

        return module;
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
            new AuthenticateMessageHandlerImpl(module.require(Orm.class))
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
                "r"
            )
        );
    }
}
