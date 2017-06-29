package tracker.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.authorization.Authorizer;
import elasta.composer.ConvertersMap;
import elasta.composer.MessageBus;
import elasta.composer.MessageProcessingErrorHandler;
import elasta.composer.builder.impl.ConvertersMapBuilderImpl;
import elasta.composer.converter.FlowToJsonObjectMessageHandlerConverter;
import elasta.composer.converter.impl.FlowToJsonObjectMessageHandlerConverterImpl;
import elasta.composer.impl.MessageBusImpl;
import elasta.composer.message.handlers.builder.impl.AddMessageHandlerBuilderImpl;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.model.response.builder.ValidationErrorModelBuilder;
import elasta.composer.model.response.builder.ValidationResultModelBuilder;
import elasta.composer.model.response.builder.impl.AuthorizationErrorModelBuilderImpl;
import elasta.composer.model.response.builder.impl.ValidationErrorModelBuilderImpl;
import elasta.composer.model.response.builder.impl.ValidationResultModelBuilderImpl;
import elasta.composer.state.handlers.UserIdConverter;
import elasta.composer.state.handlers.response.generator.JsonArrayResponseGenerator;
import elasta.composer.state.handlers.response.generator.JsonObjectResponseGenerator;
import elasta.core.promise.impl.Promises;
import elasta.eventbus.SimpleEventBus;
import elasta.eventbus.impl.SimpleEventBusImpl;
import elasta.module.ModuleSystem;
import elasta.module.ModuleSystemBuilder;
import elasta.orm.Orm;
import elasta.orm.OrmExporter;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.idgenerator.LongIdGenerator;
import elasta.orm.idgenerator.ObjectIdGenerator;
import elasta.orm.idgenerator.impl.LongIdGeneratorImpl;
import elasta.orm.idgenerator.impl.LongObjectIdGeneratorImpl;
import elasta.pipeline.MessageBundle;
import elasta.pipeline.util.MessageBundleImpl;
import elasta.pipeline.validator.JsonObjectValidatorAsync;
import elasta.sql.SqlDB;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import tracker.Addresses;
import tracker.App;
import tracker.AppUtils;
import tracker.StatusCodes;
import tracker.entity_config.Entities;

import java.util.Objects;

/**
 * Created by sohan on 6/25/2017.
 */
final public class AppImpl implements App {

    @Override
    public App start(Config config) {
        Objects.requireNonNull(config);

        final JDBCClient jdbcClient = JDBCClient.createShared(config.getVertx(), config.getDb());

        final ModuleSystemBuilder builder = ModuleSystem.builder();

        builder.export(Config.class, module -> module.export(config));

        builder.export(JDBCClient.class, module -> module.export(jdbcClient));

        builder.export(Vertx.class, module -> module.export(module.require(Config.class).getVertx()));
        builder.export(EventBus.class, module -> module.export(module.require(Vertx.class).eventBus()));

        exportOrm(builder, jdbcClient);

        exportAppComponents(builder, config);

        ModuleSystem module = builder.build();

        createMessageHandlers(module);

        MessageBus messageBus = module.require(MessageBus.class);

        messageBus.sendAndReceive(
            MessageBus.Params.builder()
                .address(Addresses.userCreate)
                .message(
                    new JsonObject(
                        "{\n" +
                            "  \"userId\" : \"admin-2\",\n" +
                            "  \"username\" : \"fakmin\",\n" +
                            "  \"email\" : \"fakmin@fakmin\",\n" +
                            "  \"phone\" : \"01951883413\",\n" +
                            "  \"createDate\" : \"2017-06-07 00:00:00\",\n" +
                            "  \"updateDate\" : \"2017-06-22 00:00:00\"\n" +
                            "}"
                    ).put("createdBy", new JsonObject(ImmutableMap.of(
                        "id", 1
                    ))).put("updatedBy", new JsonObject(ImmutableMap.of(
                        "id", 1
                    )))
                )
                .userId("admin-1")
                .build()
        ).then(objectMessage -> {
            System.out.println(objectMessage.body());
            System.out.println(objectMessage.headers());
        });

        return this;
    }

    private ModuleSystem createMessageHandlers(ModuleSystem module) {

        EventBus eventBus = module.require(EventBus.class);

        eventBus.consumer(
            Addresses.userCreate,
            new AddMessageHandlerBuilderImpl(
                module.require(Authorizer.class),
                module.require(ConvertersMap.class),
                Addresses.userCreate,
                module.require(AuthorizationErrorModelBuilder.class),
                Entities.USER,
                module.require(EntityMappingHelper.class).getPrimaryKey(Entities.USER),
                asyncUserValidator(),
                module.require(ValidationErrorModelBuilder.class),
                module.require(Orm.class),
                module.require(MessageBus.class),
                Addresses.post(Addresses.userCreate),
                module.require(JsonObjectResponseGenerator.class),
                module.require(FlowToJsonObjectMessageHandlerConverter.class),
                module.require(ObjectIdGenerator.class),
                module.require(SqlDB.class)
            ).build()
        );

        return module;
    }

    private ModuleSystemBuilder exportAppComponents(ModuleSystemBuilder builder, Config config) {

        exportAuthorizer(builder);

        builder.export(ConvertersMap.class, module -> {
            module.export(new ConvertersMapBuilderImpl().build());
        });

        builder.export(AuthorizationErrorModelBuilder.class, module -> {
            module.export(new AuthorizationErrorModelBuilderImpl(
                StatusCodes.AUTHORIZATION_ERROR,
                module.require(MessageBundle.class)
            ));
        });

        exportValidationErrorModelBuilder(builder, config);

        builder.export(MessageBus.class, module -> module.export(new MessageBusImpl(
            module.require(SimpleEventBus.class)
        )));

        exportJsonObjectResponseGenerator(builder, config);

        exportJsonArrayResponseGenerator(builder, config);

        builder.export(FlowToJsonObjectMessageHandlerConverter.class, module -> {
            module.export(new FlowToJsonObjectMessageHandlerConverterImpl(
                module.require(MessageProcessingErrorHandler.class),
                module.require(UserIdConverter.class),
                module.require(ConvertersMap.class)
            ));
        });

        exportObjectIdGenerator(builder);

        exportSimpleEventBus(builder);

        builder.export(MessageBundle.class, module -> module.export(new MessageBundleImpl(
            config.getMessageBundle()
        )));

        builder.export(MessageProcessingErrorHandler.class, module -> module.export(
            params -> params.getEx().printStackTrace()
        ));

        builder.export(UserIdConverter.class, module -> module.export(new UserIdConverterImpl()));

        return builder;
    }

    private ModuleSystemBuilder exportObjectIdGenerator(ModuleSystemBuilder builder) {

        builder.export(ObjectIdGenerator.class, module -> module.export(
            new LongObjectIdGeneratorImpl(
                module.require(EntityMappingHelper.class),
                module.require(LongIdGenerator.class),
                AppUtils.isNewKey
            )
        ));

        builder.export(LongIdGenerator.class, module -> module.export(new LongIdGeneratorImpl()));

        return builder;
    }

    private ModuleSystemBuilder exportJsonArrayResponseGenerator(ModuleSystemBuilder builder, Config config) {

        builder.export(JsonArrayResponseGenerator.class, module -> {
            module.export(o -> ((JsonArray) o));
        });

        return builder;
    }

    private ModuleSystemBuilder exportJsonObjectResponseGenerator(ModuleSystemBuilder builder, Config config) {

        builder.export(JsonObjectResponseGenerator.class, module -> {
            module.export(o -> ((JsonObject) o));
        });

        return builder;
    }

    private ModuleSystemBuilder exportSimpleEventBus(ModuleSystemBuilder builder) {

        builder.export(SimpleEventBus.class, module -> module.export(new SimpleEventBusImpl(
            module.require(Vertx.class).eventBus()
        )));

        return builder;
    }

    private ModuleSystemBuilder exportValidationErrorModelBuilder(ModuleSystemBuilder builder, Config config) {

        builder.export(ValidationErrorModelBuilder.class, module -> {
            module.export(new ValidationErrorModelBuilderImpl(
                StatusCodes.VALIDATION_ERROR,
                module.require(MessageBundle.class),
                module.require(ValidationResultModelBuilder.class)
            ));
        });

        builder.export(ValidationResultModelBuilder.class, module -> {
            module.export(new ValidationResultModelBuilderImpl(
                module.require(MessageBundle.class)
            ));
        });

        return builder;
    }

    private ModuleSystemBuilder exportAuthorizer(ModuleSystemBuilder builder) {
        builder.export(Authorizer.class, module -> module.export(params -> Promises.of(true)));
        return builder;
    }

    private JsonObjectValidatorAsync asyncUserValidator() {
        return val -> Promises.of(ImmutableList.of());
    }

    private void exportOrm(ModuleSystemBuilder builder, JDBCClient jdbcClient) {

        OrmExporter.exportTo(
            OrmExporter.ExportToParams.builder()
                .entities(Entities.entities())
                .moduleSystemBuilder(builder)
                .jdbcClient(jdbcClient)
                .isNewKey(AppUtils.isNewKey)
                .build()
        );
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
