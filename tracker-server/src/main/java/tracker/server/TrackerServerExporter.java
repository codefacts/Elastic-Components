package tracker.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import elasta.composer.MessageBus;
import elasta.composer.MessageProcessingErrorHandler;
import elasta.composer.impl.MessageProcessingErrorHandlerImpl;
import elasta.module.ModuleExporter;
import elasta.module.ModuleSystemBuilder;
import elasta.orm.Orm;
import elasta.orm.OrmExporter;
import elasta.sql.SqlExecutor;
import elasta.sql.impl.SqlExecutorImpl;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.jdbc.JDBCClient;
import lombok.Builder;
import lombok.Value;
import tracker.*;
import tracker.StatusCodes;
import tracker.entity_config.Entities;
import tracker.server.component.AuthTokenGenerator;
import tracker.server.component.impl.AuthTokenGeneratorImpl;
import tracker.server.generators.request.MessageHeaderGenerator;
import tracker.server.generators.request.impl.MessageHeaderGeneratorImpl;
import tracker.server.generators.response.*;
import tracker.server.generators.response.impl.*;
import tracker.server.impl.ErrorHandlerImpl;
import tracker.server.interceptors.impl.AuthInterceptorImpl;
import tracker.server.listeners.*;
import tracker.server.request.handlers.LogoutRequestHandler;
import tracker.server.request.handlers.impl.LoginRequestHandlerImpl;
import tracker.server.request.handlers.impl.LogoutRequestHandlerImpl;
import tracker.server.request.handlers.impl.RequestProcessingErrorHandlerImpl;
import tracker.server.impl.StorageMapImpl;
import tracker.server.interceptors.AuthInterceptor;
import tracker.server.request.handlers.LoginRequestHandler;
import tracker.server.request.handlers.RequestProcessingErrorHandler;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static tracker.server.Uris.api;

/**
 * Created by sohan on 7/1/2017.
 */
public interface TrackerServerExporter extends ModuleExporter {

    static ModuleSystemBuilder exportTo(ExportToParams params) {
        Objects.requireNonNull(params);
        ModuleSystemBuilder builder = params.getBuilder();
        Vertx vertx = params.getVertx();
        MessageBus messageBus = params.getMessageBus();
        JDBCClient jdbcClient = params.getJdbcClient();

        System.out.println(params.getServerConfig());

        builder.export(ServerConfig.class, module -> module.export(params.getServerConfig()));

        exportListeners(builder, vertx, jdbcClient, messageBus);

        builder.export(LoginRequestHandler.class, module -> {
            module.export(new LoginRequestHandlerImpl(
                vertx,
                messageBus,
                module.require(AuthTokenGenerator.class),
                module.require(RequestProcessingErrorHandler.class)
            ));
        });

        builder.export(LogoutRequestHandler.class, module -> module.export(new LogoutRequestHandlerImpl(
            module.require(AuthTokenGenerator.class),
            module.require(RequestProcessingErrorHandler.class)
        )));

        builder.export(AuthInterceptor.class, module -> module.export(new AuthInterceptorImpl(
            module.require(AuthTokenGenerator.class),
            module.require(RequestProcessingErrorHandler.class),
            ImmutableSet.of(
                new AuthInterceptor.MethodAndUri(HttpMethod.POST, api(Uris.loginUri)),
                new AuthInterceptor.MethodAndUri(HttpMethod.GET, api(Uris.logoutUri)),
                new AuthInterceptor.MethodAndUri(HttpMethod.POST, api(Uris.deviceUri)),
                new AuthInterceptor.MethodAndUri(HttpMethod.POST, api(Uris.userUri)),
                new AuthInterceptor.MethodAndUri(HttpMethod.POST, Uris.api(Uris.androidTrackersPicturesUri))
            )
        )));

        exportResponseGenerators(builder);

        builder.export(MessageHeaderGenerator.class, module -> module.export(new MessageHeaderGeneratorImpl(ServerUtils.CUSTOM_HEADER_PREFIX)));

        exportErrorHandlers(builder);

        builder.export(AuthTokenGenerator.class, module -> module.export(new AuthTokenGeneratorImpl(
            module.require(StorageMap.class),
            params.getAuthTokenExpireTime(), TimeUnit.HOURS
        )));

        builder.export(StorageMap.class, module -> module.export(new StorageMapImpl(
            "StorageMap",
            module.require(SqlExecutor.class),
            vertx,
            TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES)
        )));

        builder.export(SqlExecutor.class, module -> module.export(new SqlExecutorImpl(
            jdbcClient
        )));

        OrmExporter.exportTo(
            OrmExporter.ExportToParams.builder()
                .isNewKey(TrackerUtils.isNewKey)
                .entities(Entities.entities())
                .moduleSystemBuilder(builder)
                .jdbcClient(jdbcClient)
                .build()
        );

        builder.export(EventBus.class, module -> module.export(vertx.eventBus()));

        return builder;
    }

    static void exportErrorHandlers(ModuleSystemBuilder builder) {

        builder.export(RequestProcessingErrorHandler.class, module -> module.export(new RequestProcessingErrorHandlerImpl()));
        builder.export(ErrorHandler.class, module -> module.export(new ErrorHandlerImpl()));
        builder.export(MessageProcessingErrorHandler.class, module -> module.export(new MessageProcessingErrorHandlerImpl(
            50, StatusCodes.unexpectedError
        )));
    }

    static void exportListeners(ModuleSystemBuilder builder, Vertx vertx, JDBCClient jdbcClient, MessageBus messageBus) {

        builder.export(AddPositionListener.class, module -> module.export(new AddPositionListenerImpl(
            module.require(NewPositionListener.class)
        )));

        builder.export(NewPositionListener.class, module -> module.export(new NewPositionListenerImpl(
            module.require(Orm.class),
            module.require(EventBus.class),
            module.require(MessageProcessingErrorHandler.class),
            module.require(ErrorHandler.class)
        )));
    }

    static void exportResponseGenerators(ModuleSystemBuilder builder) {

        builder.export(AddAllHttpResponseGenerator.class, module -> module.export(new AddAllHttpResponseGeneratorImpl(
            ImmutableList.of("id")
        )));

        builder.export(AddHttpResponseGenerator.class, module -> module.export(new AddHttpResponseGeneratorImpl(
            ImmutableList.of("id")
        )));

        builder.export(DeleteAllHttpResponseGenerator.class, module -> module.export(new DeleteAllHttpResponseGeneratorImpl()));

        builder.export(DeleteHttpResponseGenerator.class, module -> module.export(new DeleteHttpResponseGeneratorImpl()));

        builder.export(FindAllHttpResponseGenerator.class, module -> module.export(new FindAllHttpResponseGeneratorImpl()));

        builder.export(FindOneHttpResponseGenerator.class, module -> module.export(new FindOneHttpResponseGeneratorImpl()));

        builder.export(UpdateAllHttpResponseGenerator.class, module -> module.export(new UpdateAllHttpResponseGeneratorImpl()));
        builder.export(UpdateHttpResponseGenerator.class, module -> module.export(new UpdateHttpResponseGeneratorImpl()));
    }

    @Value
    @Builder
    final class ExportToParams {
        final Vertx vertx;
        final ModuleSystemBuilder builder;
        final JDBCClient jdbcClient;
        final MessageBus messageBus;
        final int authTokenExpireTime;
        final ServerConfig serverConfig;

        public ExportToParams(Vertx vertx, ModuleSystemBuilder builder, JDBCClient jdbcClient, MessageBus messageBus, int authTokenExpireTime, ServerConfig serverConfig) {
            Objects.requireNonNull(vertx);
            Objects.requireNonNull(builder);
            Objects.requireNonNull(jdbcClient);
            Objects.requireNonNull(messageBus);
            Objects.requireNonNull(serverConfig);
            this.vertx = vertx;
            this.builder = builder;
            this.jdbcClient = jdbcClient;
            this.messageBus = messageBus;
            this.authTokenExpireTime = authTokenExpireTime;
            this.serverConfig = serverConfig;
        }
    }
}
