package tracker;

import elasta.authorization.Authorizer;
import elasta.composer.*;
import elasta.composer.builder.impl.ConvertersMapBuilderImpl;
import elasta.composer.converter.*;
import elasta.composer.converter.impl.*;
import elasta.composer.impl.AppDateTimeFormatterImpl;
import elasta.composer.impl.MessageBusImpl;
import elasta.composer.impl.MessageProcessingErrorHandlerImpl;
import elasta.composer.interceptor.DbOperationInterceptor;
import elasta.composer.interceptor.impl.DbOperationInterceptorImpl;
import elasta.composer.model.response.builder.*;
import elasta.composer.model.response.builder.impl.*;
import elasta.composer.converter.UserIdConverter;
import elasta.composer.respose.generator.JsonArrayResponseGenerator;
import elasta.composer.respose.generator.JsonObjectResponseGenerator;
import elasta.composer.respose.generator.ResponseGenerator;
import elasta.composer.respose.generator.impl.JsonArrayResponseGeneratorImpl;
import elasta.composer.respose.generator.impl.JsonObjectResponseGeneratorImpl;
import elasta.composer.respose.generator.impl.ResponseGeneratorImpl;
import elasta.composer.state.handlers.*;
import elasta.composer.state.handlers.impl.*;
import elasta.core.promise.impl.Promises;
import elasta.eventbus.SimpleEventBus;
import elasta.eventbus.impl.SimpleEventBusImpl;
import elasta.module.ModuleExporter;
import elasta.module.ModuleSystemBuilder;
import elasta.orm.OrmExporter;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.idgenerator.LongIdGenerator;
import elasta.orm.idgenerator.ObjectIdGenerator;
import elasta.orm.idgenerator.impl.RandomLongIdGeneratorImpl;
import elasta.orm.idgenerator.impl.LongObjectIdGeneratorImpl;
import elasta.orm.idgenerator.impl.TimestampBasedLongIdGenerator;
import elasta.pipeline.MessageBundle;
import elasta.pipeline.util.MessageBundleImpl;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.jdbc.JDBCClient;
import lombok.Builder;
import lombok.Value;
import tracker.entity_config.Entities;
import tracker.impl.AppHelpersImpl;
import tracker.impl.FlowBuilderHelperImpl;
import tracker.impl.MessageHandlersBuilderImpl;
import tracker.impl.UserIdConverterImpl;
import tracker.model.BaseTable;

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

        EntityToStateHandlersMapExporter.exportTo(builder);

        return builder;
    }

    static void exportExtraComponents(ModuleSystemBuilder builder, App.Config config) {

        builder.export(MessageHandlersBuilder.class, module -> {
            module.export(new MessageHandlersBuilderImpl(
                module.require(FlowBuilderHelper.class)
            ));
        });

        builder.export(FlowBuilderHelper.class, module -> module.export(
            new FlowBuilderHelperImpl(module.require(EntityToStateHandlersMap.class))
        ));

        builder.export(AppHelpers.class, module -> {
            module.export(new AppHelpersImpl(
                module.require(App.Config.class).getRootAlias(), module.require(EntityMappingHelper.class)
            ));
        });
    }

    static ModuleSystemBuilder exportAppComponents(ModuleSystemBuilder builder, App.Config config) {

        exportStartStateHandler(builder, config);

        exportEndStateHandler(builder, config);

        exportAuthorizer(builder);

        builder.export(BeforeFindAllStateHandler.class, module -> module.export(new BeforeFindAllStateHandlerImpl()));

        builder.export(BeforeAddStateHandler.class, module -> module.export(new BeforeAddStateHandlerImpl()));

        builder.export(BeforeAddAllStateHandler.class, module -> module.export(new BeforeAddAllStateHandlerImpl()));

        builder.export(BeforeUpdateStateHandler.class, module -> module.export(new BeforeUpdateStateHandlerImpl()));

        builder.export(BeforeUpdateAllStateHandler.class, module -> module.export(new BeforeUpdateAllStateHandlerImpl()));

        builder.export(ConvertersMap.class, module -> {
            module.export(new ConvertersMapBuilderImpl().build());
        });

        builder.export(AuthorizationErrorModelBuilder.class, module -> {
            module.export(new AuthorizationErrorModelBuilderImpl(
                StatusCodes.authorizationSuccess,
                module.require(MessageBundle.class)
            ));
        });

        builder.export(AuthorizationSuccessModelBuilder.class, module -> module.export(
            new AuthorizationSuccessModelBuilderImpl(
                StatusCodes.authorizationSuccess, module.require(MessageBundle.class)
            )
        ));

        exportValidationSuccessModelBuilder(builder, config);

        exportValidationErrorModelBuilder(builder, config);

        builder.export(ConversionToCriteriaStateHandler.class, module -> module.export(
            new ConversionToCriteriaStateHandlerImpl(
                module.require(App.Config.class).getRootAlias()
            )
        ));

        builder.export(MessageBus.class, module -> module.export(new MessageBusImpl(
            module.require(SimpleEventBus.class)
        )));

        exportResponseGenerator(builder, config);

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

        builder.export(JsonObjectToPageRequestConverter.class, module -> module.export(
            new JsonObjectToPageRequestConverterImpl(
                config.getDefaultPage(),
                config.getDefaultPageSize()
            )
        ));

        builder.export(JsonObjectToQueryParamsConverter.class, module -> module.export(
            new JsonObjectToQueryParamsConverterImpl()
        ));

        builder.export(PageModelBuilder.class, module -> module.export(
            new PageModelBuilderImpl()
        ));

        exportObjectIdGenerator(builder);

        exportSimpleEventBus(builder);

        builder.export(MessageBundle.class, module -> module.export(new MessageBundleImpl(
            config.getMessageBundle()
        )));

        builder.export(MessageProcessingErrorHandler.class, module -> module.export(
            new MessageProcessingErrorHandlerImpl(
                TrackerUtils.failureCode, StatusCodes.badRequestError
            )
        ));

        builder.export(UserIdConverter.class, module -> module.export(new UserIdConverterImpl()));

        builder.export(DbOperationInterceptor.class, module -> module.export(
            new DbOperationInterceptorImpl(
                module.require(AppDateTimeFormatter.class),
                BaseTable.created_by,
                BaseTable.updated_by,
                BaseTable.create_date,
                BaseTable.update_date
            )
        ));

        builder.export(AppDateTimeFormatter.class, module -> module.export(new AppDateTimeFormatterImpl()));

        return builder;
    }

    static void exportValidationSuccessModelBuilder(ModuleSystemBuilder builder, App.Config config) {
        builder.export(ValidationSuccessModelBuilder.class, module -> module.export(
            new ValidationSuccessModelBuilderImpl(
                module.require(MessageBundle.class)
            )
        ));
    }

    static void exportResponseGenerator(ModuleSystemBuilder builder, App.Config config) {
        builder.export(ResponseGenerator.class, module -> module.export(
            new ResponseGeneratorImpl()
        ));
    }

    static void exportEndStateHandler(ModuleSystemBuilder builder, App.Config config) {
        builder.export(EndStateHandler.class, module -> module.export(
            new EndStateHandlerImpl()
        ));
    }

    static void exportStartStateHandler(ModuleSystemBuilder builder, App.Config config) {
        builder.export(StartStateHandler.class, module -> module.export(new StartStateHandlerImpl()));
    }

    static ModuleSystemBuilder exportObjectIdGenerator(ModuleSystemBuilder builder) {

        builder.export(ObjectIdGenerator.class, module -> module.export(
            new LongObjectIdGeneratorImpl(
                module.require(EntityMappingHelper.class),
                module.require(LongIdGenerator.class),
                TrackerUtils.isNewKey
            )
        ));

        builder.export(LongIdGenerator.class, module -> module.export(new TimestampBasedLongIdGenerator()));

        return builder;
    }

    static ModuleSystemBuilder exportJsonArrayResponseGenerator(ModuleSystemBuilder builder, App.Config config) {

        builder.export(JsonArrayResponseGenerator.class, module -> {
            module.export(new JsonArrayResponseGeneratorImpl());
        });

        return builder;
    }

    static ModuleSystemBuilder exportJsonObjectResponseGenerator(ModuleSystemBuilder builder, App.Config config) {

        builder.export(JsonObjectResponseGenerator.class, module -> {
            module.export(new JsonObjectResponseGeneratorImpl());
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
                StatusCodes.validationError,
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
                .isNewKey(TrackerUtils.isNewKey)
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
