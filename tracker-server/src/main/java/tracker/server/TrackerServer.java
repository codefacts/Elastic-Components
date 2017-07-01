package tracker.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import elasta.composer.MessageBus;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import tracker.App;
import tracker.impl.AppImpl;
import tracker.server.ex.ConfigLoaderException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Created by sohan on 7/1/2017.
 */
public interface TrackerServer {
    JsonObject config = loadConfig("config.json");
    int DEFAULT_PORT = 152;

    static void main(String[] asfd) {

        System.out.println(config.encodePrettily());

        Vertx vertx = Vertx.vertx();

        MessageBus messageBus = messageBus(vertx);

        createWebServer(vertx, messageBus);
    }

    static void configureRouter(Vertx vertx, Router router, MessageBus messageBus) {
        router.post("auth")
            .handler(BodyHandler.create())
            .handler(
                reqHanlder(
                    new AuthHandler(vertx, messageBus)
                )
            );
    }

    static Handler<RoutingContext> reqHanlder(RequestHandler requestHandler) {
        return RequestHandler.create(
            requestHandler
        );
    }

    static void createWebServer(Vertx vertx, MessageBus messageBus) {

        HttpServer httpServer = vertx.createHttpServer();

        Router router = createRouter(vertx);

        configureRouter(vertx, router, messageBus);

        httpServer.requestHandler(router::accept);

        httpServer.listen(config.getInteger("port", DEFAULT_PORT));
    }

    static Router createRouter(Vertx vertx) {

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
                new JsonObject(
                    ImmutableMap.of(
                        "user", "root",
                        "password", "",
                        "driver_class", "com.mysql.jdbc.Driver",
                        "url", "jdbc:mysql://localhost/tracker_db"
                    )
                ),
                ImmutableMap.of(),
                vertx,
                1,
                10,
                "r"
            )
        ).mesageBus();
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
