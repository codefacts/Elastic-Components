package elasta.composer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import elasta.composer.module_exporter.ComposerExporter;
import elasta.composer.module_exporter.DbEventHandlers;
import elasta.core.eventbus.SimpleEventBus;
import elasta.core.eventbus.impl.SimpleEventBusImpl;
import elasta.module.ModuleSystem;
import elasta.Orm;
import elasta.OrmExporter;
import elasta.sql.core.FieldInfoBuilder;
import elasta.webutils.app.utils.PerRequestToEventResolver;
import elasta.webutils.app.RequestHandler;
import elasta.webutils.app.module.exporter.WebUtilsExporter;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import jpatest.core.Main;
import jpatest.core.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created by Jango on 11/9/2016.
 */
public class App {
    public static final String API_PREFIX = "/api/*";

    public static void main(String[] args) {

        final ModuleSystem moduleSystem = ModuleSystem.create();

        WebUtilsExporter.get().exportTo(moduleSystem);
        ComposerExporter.get().exportTo(moduleSystem);
        OrmExporter.get().exportTo(moduleSystem);
        moduleSystem.export(SimpleEventBus.class, module -> module.export(new SimpleEventBusImpl()));
        moduleSystem.export(EntityManagerFactory.class, module -> module.export(Persistence.createEntityManagerFactory(Main.PU)));
        moduleSystem.export(Vertx.class, module -> module.export(Vertx.vertx()));
        moduleSystem.export(ObjectMapper.class, module -> module.export(new ObjectMapper()));
        moduleSystem.export(JsonObject.class, "db-config", module -> module.export(Test.DB_CONFIG));
        //---------------
        DbEventHandlers.registerHandlers(moduleSystem.require(SimpleEventBus.class), moduleSystem.require(Orm.class));

        moduleSystem.require(SimpleEventBus.class)
            .addProcessorP(Cnsts.API + ".{" + EventToFlowDispatcher.RESOURCE + "}.{" + EventToFlowDispatcher.ACTION + "}",
                moduleSystem.require(EventToFlowDispatcher.class)
            );

        Vertx vertx = moduleSystem.require(Vertx.class);

        Router router = Router.router(vertx);

//        router.get().handler(event -> event.response().end("ok"));

        router.get(api("/tablet/:id")).handler(new PerRequestToEventResolver(apiEvent("tablet.find")));
        router.get(api("/tablet")).handler(new PerRequestToEventResolver(apiEvent("tablet.find-all")));
        router.post(api("/tablet")).handler(new PerRequestToEventResolver(apiEvent("tablet.create")));
        router.put(api("/tablet/:id")).handler(new PerRequestToEventResolver(apiEvent("tablet.update")));
        router.patch(api("/tablet/:id")).handler(new PerRequestToEventResolver(apiEvent("tablet.update")));
        router.delete(api("/tablet/:id")).handler(new PerRequestToEventResolver(apiEvent("tablet.delete")));

        router.post(API_PREFIX).handler(BodyHandler.create());
        router.put(API_PREFIX).handler(BodyHandler.create());
        router.patch(API_PREFIX).handler(BodyHandler.create());
        router.delete(API_PREFIX).handler(BodyHandler.create());
        router.route(API_PREFIX).handler(moduleSystem.require(RequestHandler.class));

        vertx.createHttpServer().requestHandler(router::accept).listen(85);
        System.out.println("Server Started.");

        moduleSystem.require(Orm.class).findOne("Tablet", 1, ImmutableList.of(
            new FieldInfoBuilder()
                .setFields(ImmutableList.of("id", "name"))
                .createSqlField()
        )).then(jsonObject -> System.out.println(jsonObject.encodePrettily())).err(Throwable::printStackTrace);
    }

    private static String apiEvent(String event) {
        return Cnsts.API + "." + event.replace('-', '.');
    }

    private static String api(String uri) {
        return "/api" + uri;
    }
}
