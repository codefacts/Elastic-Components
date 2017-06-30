package tracker;

import com.google.common.collect.ImmutableList;
import elasta.authorization.Authorizer;
import elasta.composer.ConvertersMap;
import elasta.composer.MessageBus;
import elasta.composer.MessageProcessingErrorHandler;
import elasta.composer.builder.impl.ConvertersMapBuilderImpl;
import elasta.composer.converter.FlowToJsonArrayMessageHandlerConverter;
import elasta.composer.converter.FlowToJsonObjectMessageHandlerConverter;
import elasta.composer.converter.FlowToMessageHandlerConverter;
import elasta.composer.converter.impl.FlowToJsonArrayMessageHandlerConverterImpl;
import elasta.composer.converter.impl.FlowToJsonObjectMessageHandlerConverterImpl;
import elasta.composer.converter.impl.FlowToMessageHandlerConverterImpl;
import elasta.composer.impl.MessageBusImpl;
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
import elasta.module.ModuleExporter;
import elasta.module.ModuleSystemBuilder;
import elasta.orm.OrmExporter;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.idgenerator.LongIdGenerator;
import elasta.orm.idgenerator.ObjectIdGenerator;
import elasta.orm.idgenerator.impl.LongIdGeneratorImpl;
import elasta.orm.idgenerator.impl.LongObjectIdGeneratorImpl;
import elasta.pipeline.MessageBundle;
import elasta.pipeline.util.MessageBundleImpl;
import elasta.pipeline.validator.JsonObjectValidatorAsync;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import lombok.Builder;
import lombok.Value;
import tracker.entity_config.Entities;
import tracker.impl.AppHelpersImpl;
import tracker.impl.MessageHandlersBuilderImpl;
import tracker.impl.UserIdConverterImpl;

import java.util.Objects;

/**
 * Created by sohan on 6/30/2017.
 */
public interface TrackerExporter extends ModuleExporter {

    static ModuleSystemBuilder exportTo(ExportToParams params) {

        Objects.requireNonNull(params);

        final ModuleSystemBuilder builder = params.getBuilder();
        final App.Config config = params.getConfig();
        final JDBCClient jdbcClient = JDBCClient.createShared(config.getVertx(), config.getDb());


        params.getBuilder().export(App.Config.class, module -> module.export(config));

        params.getBuilder().export(JDBCClient.class, module -> module.export(jdbcClient));

        builder.export(Vertx.class, module -> module.export(module.require(App.Config.class).getVertx()));
        builder.export(EventBus.class, module -> module.export(module.require(Vertx.class).eventBus()));

        exportOrm(builder, jdbcClient);

        exportAppComponents(builder, config);

        exportExtraComponents(builder, config);

        return builder;
    }

    static void exportExtraComponents(ModuleSystemBuilder builder, App.Config config) {

        builder.export(MessageHandlersBuilder.class, module -> {
            module.export(new MessageHandlersBuilderImpl());
        });

        builder.export(AppHelpers.class, module -> {
            module.export(new AppHelpersImpl(
                module.require(EntityMappingHelper.class)
            ));
        });
    }

    static ModuleSystemBuilder exportAppComponents(ModuleSystemBuilder builder, App.Config config) {

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

        builder.export(FlowToJsonArrayMessageHandlerConverter.class, module -> {
            module.export(new FlowToJsonArrayMessageHandlerConverterImpl(
                module.require(MessageProcessingErrorHandler.class),
                module.require(UserIdConverter.class),
                module.require(ConvertersMap.class)
            ));
        });

        builder.export(FlowToMessageHandlerConverter.class, module -> {
            module.export(new FlowToMessageHandlerConverterImpl(
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

    static ModuleSystemBuilder exportObjectIdGenerator(ModuleSystemBuilder builder) {

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

    static ModuleSystemBuilder exportJsonArrayResponseGenerator(ModuleSystemBuilder builder, App.Config config) {

        builder.export(JsonArrayResponseGenerator.class, module -> {
            module.export(o -> ((JsonArray) o));
        });

        return builder;
    }

    static ModuleSystemBuilder exportJsonObjectResponseGenerator(ModuleSystemBuilder builder, App.Config config) {

        builder.export(JsonObjectResponseGenerator.class, module -> {
            module.export(o -> ((JsonObject) o));
        });

        return builder;
    }

    static ModuleSystemBuilder exportSimpleEventBus(ModuleSystemBuilder builder) {

        builder.export(SimpleEventBus.class, module -> module.export(new SimpleEventBusImpl(
            module.require(Vertx.class).eventBus()
        )));

        return builder;
    }

    static ModuleSystemBuilder exportValidationErrorModelBuilder(ModuleSystemBuilder builder, App.Config config) {

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

    static ModuleSystemBuilder exportAuthorizer(ModuleSystemBuilder builder) {
        builder.export(Authorizer.class, module -> module.export(params -> Promises.of(true)));
        return builder;
    }

    static void exportOrm(ModuleSystemBuilder builder, JDBCClient jdbcClient) {

        OrmExporter.exportTo(
            OrmExporter.ExportToParams.builder()
                .entities(Entities.entities())
                .moduleSystemBuilder(builder)
                .jdbcClient(jdbcClient)
                .isNewKey(AppUtils.isNewKey)
                .build()
        );
    }

    @Value
    @Builder
    final class ExportToParams {
        ModuleSystemBuilder builder;
        App.Config config;

        public ExportToParams(ModuleSystemBuilder builder, App.Config config) {
            Objects.requireNonNull(builder);
            Objects.requireNonNull(config);
            this.builder = builder;
            this.config = config;
        }
    }
}
