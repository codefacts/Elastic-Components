package tracker.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.authorization.Authorizer;
import elasta.composer.ConvertersMap;
import elasta.composer.MessageBus;
import elasta.composer.converter.FlowToJsonObjectMessageHandlerConverter;
import elasta.composer.converter.FlowToMessageHandlerConverter;
import elasta.composer.message.handlers.builder.impl.AddMessageHandlerBuilderImpl;
import elasta.composer.message.handlers.builder.impl.FindOneMessageHandlerBuilderImpl;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.model.response.builder.ValidationErrorModelBuilder;
import elasta.composer.state.handlers.response.generator.JsonObjectResponseGenerator;
import elasta.core.promise.impl.Promises;
import elasta.module.ModuleSystem;
import elasta.module.ModuleSystemBuilder;
import elasta.orm.Orm;
import elasta.orm.idgenerator.ObjectIdGenerator;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import elasta.pipeline.validator.JsonObjectValidatorAsync;
import elasta.sql.SqlDB;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import tracker.*;
import tracker.entity_config.Entities;
import tracker.model.UserModel;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

        module.require(MessageHandlersBuilder.class).build(module).forEach(addressAndHandler -> {
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
                Vertx.vertx()
            )
        );
    }
}
