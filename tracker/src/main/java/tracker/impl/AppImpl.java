package tracker.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.MessageBus;
import elasta.module.ModuleSystem;
import elasta.module.ModuleSystemBuilder;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import tracker.*;
import tracker.entity_config.Entities;
import tracker.model.UserModel;

import java.util.Objects;

/**
 * Created by sohan on 6/25/2017.
 */
final public class AppImpl implements App {

    @Override
    public App start(Config config) {
        Objects.requireNonNull(config);

        final ModuleSystemBuilder builder = ModuleSystem.builder();

        TrackerExporter.exportTo(
            TrackerExporter.ExportToParams.builder()
                .builder(builder)
                .config(config)
                .build()
        );

        ModuleSystem module = builder.build();

        createMessageHandlers(module);

        MessageBus messageBus = module.require(MessageBus.class);

        return this;
    }

    private ModuleSystem createMessageHandlers(ModuleSystem module) {

        EventBus eventBus = module.require(EventBus.class);

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
        ).forEach(addressAndHandler -> {
            eventBus.consumer(addressAndHandler.getAddress(), addressAndHandler.getMessageHandler());
        });

        return module;
    }

    @Override
    public App stop() {
        return this;
    }

    public static void main(String[] asfd) {
        new AppImpl().start(
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
