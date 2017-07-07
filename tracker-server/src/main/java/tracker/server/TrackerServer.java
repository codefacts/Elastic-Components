package tracker.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import elasta.composer.MessageBus;
import elasta.module.ModuleSystem;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import tracker.Addresses;
import tracker.App;
import tracker.entity_config.Entities;
import tracker.impl.AppImpl;
import tracker.model.BaseModel;
import tracker.model.DeviceModel;
import tracker.model.UserModel;
import tracker.server.ex.ConfigLoaderException;
import tracker.server.generators.response.*;
import tracker.server.generators.response.impl.AddHttpResponseGeneratorImpl;
import tracker.server.interceptors.AuthInterceptor;
import tracker.server.request.handlers.LoginRequestHandler;
import tracker.server.request.handlers.RequestHandler;
import tracker.server.request.handlers.RequestProcessingErrorHandler;
import tracker.server.request.handlers.impl.DispatchingRequestHandlerImpl;
import tracker.server.request.handlers.impl.JoDispatchingRequestHandlerImpl;
import tracker.server.request.handlers.impl.LongDispatchingRequestHandlerImpl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Objects;

import static tracker.server.Uris.api;
import static tracker.server.Uris.singularUri;

/**
 * Created by sohan on 7/1/2017.
 */
public interface TrackerServer {
    JsonObject config = loadConfig("config.json");
    int DEFAULT_PORT = 152;
    Vertx vertx = Vertx.vertx();
    MessageBus messageBus = messageBus(vertx);
    ModuleSystem module = createModule(vertx, messageBus);

    static void main(String[] asfd) {

        HttpServer server = createWebServer();

        server.listen(config.getInteger("port", DEFAULT_PORT));

        System.out.println("SERVER STARTED AT PORT: " + config.getInteger("port", DEFAULT_PORT));
    }

    static void addHandlers(Router router) {

        {
            final String loginApi = api(Uris.loginUri);

            router.post(loginApi)
                .handler(
                    reqHanlder(
                        module.require(LoginRequestHandler.class)
                    )
                );
        }

        {
            final String uri = api(Uris.userUri);
            final String singularUri = singularUri(uri);

            router.post(uri).handler(addHandler(Entities.USER, ImmutableList.of(UserModel.id, UserModel.userId, UserModel.username)));

            router.patch(singularUri).handler(updateHandler(Entities.USER));

            router.delete(singularUri).handler(deleteHandler(Entities.USER));

            router.get(singularUri).handler(findOneHandler(Entities.USER));

            router.get(uri).handler(findAllHandler(Entities.USER));
        }

        {
            final String uri = api(Uris.deviceUri);
            final String singularUri = singularUri(uri);

            router.post(uri).handler(addHandler(Entities.DEVICE, ImmutableList.of(DeviceModel.id, DeviceModel.deviceId)));

            router.get(singularUri).handler(findOneHandler(Entities.DEVICE));

            router.get(uri).handler(findAllHandler(Entities.DEVICE));
        }

        {
            final String uri = api(Uris.positionUri);
            final String singularUri = singularUri(uri);

            router.post(uri).handler(addHandler(Entities.POSITION, ImmutableList.of(BaseModel.id)));

            router.get(singularUri).handler(findOneHandler(Entities.POSITION));

            router.get(uri).handler(findAllHandler(Entities.POSITION));
        }
    }

    static Handler<RoutingContext> findAllHandler(String entity) {
        return reqHanlder(
            new DispatchingRequestHandlerImpl(
                ctx -> {

                    String jsonCriteriaStr = ctx.request().getParam(ReqParams.query);

                    if (jsonCriteriaStr == null || jsonCriteriaStr.isEmpty()) {
                        return ServerUtils.emptyJsonObject();
                    }

                    return new JsonObject(jsonCriteriaStr);
                },
                module.require(FindAllHttpResponseGenerator.class),
                module.require(RequestProcessingErrorHandler.class),
                messageBus,
                Addresses.findAll(entity)
            )
        );
    }

    static Handler<RoutingContext> findOneHandler(String entity) {
        return reqHanlder(
            new DispatchingRequestHandlerImpl(
                ctx -> new JsonObject(
                    ImmutableMap.of(
                        BaseModel.id, Long.parseLong(ctx.pathParam(PathParams.id))
                    )
                ),
                module.require(FindOneHttpResponseGenerator.class),
                module.require(RequestProcessingErrorHandler.class),
                messageBus,
                Addresses.findOne(entity)
            )
        );
    }

    static Handler<RoutingContext> deleteHandler(String entity) {
        return reqHanlder(
            new LongDispatchingRequestHandlerImpl(
                module.require(DeleteHttpResponseGenerator.class),
                module.require(RequestProcessingErrorHandler.class),
                messageBus,
                Addresses.delete(entity),
                PathParams.id
            )
        );
    }

    static RequestHandler updateHandler(String entity) {
        return reqHanlder(
            new DispatchingRequestHandlerImpl(
                ctx -> {
                    long id = Long.parseLong(ctx.pathParam(PathParams.id));
                    return ctx.getBodyAsJson().put(BaseModel.id, id);
                },
                module.require(UpdateHttpResponseGenerator.class),
                module.require(RequestProcessingErrorHandler.class),
                messageBus,
                Addresses.update(entity),
                ServerUtils.APPLICATION_JSON
            )
        );
    }

    static RequestHandler addHandler(String entity) {
        return addHandler(entity, ImmutableList.of("id"));
    }

    static RequestHandler addHandler(String entity, Collection<String> fields) {
        return reqHanlder(
            new JoDispatchingRequestHandlerImpl(
                new AddHttpResponseGeneratorImpl(fields),
                module.require(RequestProcessingErrorHandler.class),
                messageBus,
                Addresses.add(entity)
            )
        );
    }

    static RequestHandler reqHanlder(RequestHandler requestHandler) {
        return RequestHandler.create(
            requestHandler,
            module.require(RequestProcessingErrorHandler.class)
        );
    }

    static HttpServer createWebServer() {

        HttpServer httpServer = vertx.createHttpServer();

        Router router = createRouter();

        addInterceptors(router);

        addHandlers(router);

        addStaticFileHandlers(router);

        httpServer.requestHandler(router::accept);

        return httpServer;
    }

    static void addStaticFileHandlers(Router router) {

        String public_content_directory = config.getString("public_content_directory");

        if (public_content_directory == null || public_content_directory.trim().isEmpty()) {
            public_content_directory = new File("").getAbsolutePath();
        }

        router.get("/public/*").handler(
            StaticHandler.create(public_content_directory).setDirectoryListing(true)
        );
    }

    static void addInterceptors(Router router) {

        router.post().handler(BodyHandler.create());
        router.put().handler(BodyHandler.create());
        router.patch().handler(BodyHandler.create());
        router.delete().handler(BodyHandler.create());

        router.route().handler(reqHanlder(
            ctx -> {
                ctx.response().putHeader("Access-Control-Allow-Origin", "*");
                ctx.response().putHeader("Access-Control-Allow-Methods", "*");
                ctx.response().putHeader("Access-Control-Allow-Headers", "*");
                ctx.response().putHeader("Access-Control-Max-Age", "86400");
                ctx.next();
            }
        ));

        router.route(api("/*")).handler(reqHanlder(
            module.require(AuthInterceptor.class)
        ));
    }

    static Router createRouter() {

        Router router = Router.router(vertx);

        router.route().handler(CookieHandler.create());

        SessionStore store = LocalSessionStore.create(vertx);

        SessionHandler sessionHandler = SessionHandler.create(store);

        router.route().handler(sessionHandler);

        return router;
    }

    static MessageBus messageBus(Vertx vertx) {

        return new AppImpl(
            new App.Config(
                getDbConfig(),
                ImmutableMap.of(),
                vertx,
                1,
                10,
                "r"
            )
        ).mesageBus();
    }

    static ModuleSystem createModule(Vertx vertx, MessageBus messageBus) {
        return TrackerServerExporter.exportTo(
            TrackerServerExporter.ExportToParams.builder()
                .builder(ModuleSystem.builder())
                .jdbcClient(JDBCClient.createShared(vertx, getDbConfig()))
                .messageBus(messageBus)
                .vertx(vertx)
                .build()
        ).build();
    }

    static JsonObject getDbConfig() {
        return config.getJsonObject("db");
    }

    static JsonObject loadConfig(String filename) {

        try {

            final File configDir = new File("").getAbsoluteFile();

            System.out.println("configDir: " + configDir);

            File file = new File(configDir, filename);

            if (file.exists()) {

                final String jsonStr = Files.toString(file, StandardCharsets.UTF_8);

                return parseConfig(new JsonObject(jsonStr), filename);
            }

            return parseConfig(
                new JsonObject(
                    toString(TrackerServer.class.getResourceAsStream("/" + filename))
                ),
                filename
            );

        } catch (Exception e) {
            throw new ConfigLoaderException("Error loading configuration: " + e.toString(), e);
        }
    }

    static String toString(InputStream resourceAsStream) throws Exception {
        Objects.requireNonNull(resourceAsStream);
        try {
            return CharStreams.toString(new InputStreamReader(
                resourceAsStream
            ));
        } finally {
            resourceAsStream.close();
        }
    }

    static JsonObject parseConfig(JsonObject jsonObject, String filename) {

        final String profile = jsonObject.getString("profile");

        if (profile == null) {
            throw new ConfigLoaderException("No profile is specified in '" + filename + "'");
        }

        final JsonObject config = jsonObject.getJsonObject(profile);

        if (config == null) {
            throw new ConfigLoaderException("No config for profile '" + profile + "' is found in '" + filename + "'");
        }

        return config;
    }
}
