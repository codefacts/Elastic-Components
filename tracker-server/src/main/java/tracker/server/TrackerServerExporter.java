package tracker.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import elasta.composer.MessageBus;
import elasta.module.ModuleExporter;
import elasta.module.ModuleSystemBuilder;
import elasta.sql.SqlExecutor;
import elasta.sql.impl.SqlExecutorImpl;
import io.vertx.core.Vertx;
import io.vertx.ext.jdbc.JDBCClient;
import lombok.Builder;
import lombok.Value;
import tracker.server.component.AuthTokenGenerator;
import tracker.server.component.impl.AuthTokenGeneratorImpl;
import tracker.server.generators.response.*;
import tracker.server.generators.response.impl.*;
import tracker.server.interceptors.impl.AuthInterceptorImpl;
import tracker.server.request.handlers.impl.LoginRequestHandlerImpl;
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

        builder.export(LoginRequestHandler.class, module -> {
            module.export(new LoginRequestHandlerImpl(
                vertx,
                messageBus,
                module.require(AuthTokenGenerator.class),
                module.require(RequestProcessingErrorHandler.class)
            ));
        });

        builder.export(AuthInterceptor.class, module -> module.export(new AuthInterceptorImpl(
            module.require(AuthTokenGenerator.class),
            module.require(RequestProcessingErrorHandler.class),
            ImmutableSet.of(api(Uris.loginUri))
        )));

        exportResponseGenerators(builder);

        builder.export(RequestProcessingErrorHandler.class, module -> module.export(new RequestProcessingErrorHandlerImpl()));

        builder.export(AuthTokenGenerator.class, module -> module.export(new AuthTokenGeneratorImpl(
            module.require(StorageMap.class),
            12, TimeUnit.HOURS
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

        return builder;
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

        public ExportToParams(Vertx vertx, ModuleSystemBuilder builder, JDBCClient jdbcClient, MessageBus messageBus) {
            Objects.requireNonNull(vertx);
            Objects.requireNonNull(builder);
            Objects.requireNonNull(jdbcClient);
            Objects.requireNonNull(messageBus);
            this.vertx = vertx;
            this.builder = builder;
            this.jdbcClient = jdbcClient;
            this.messageBus = messageBus;
        }
    }
}
