package elasta.composer;

import elasta.composer.moc.MocDb;
import elasta.composer.module_exporter.ComposerExporter;
import elasta.composer.module_exporter.DbEventHandlers;
import elasta.composer.module_exporter.Flows;
import elasta.composer.module_exporter.States;
import elasta.core.eventbus.SimpleEventBus;
import elasta.core.eventbus.impl.SimpleEventBusImpl;
import elasta.core.flow.Flow;
import elasta.module.ModuleSystem;
import elasta.orm.Db;
import elasta.orm.jpa.DbImpl;
import elasta.webutils.app.RequestHandler;
import elasta.webutils.app.module.exporter.WebUtilsExporter;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Created by Jango on 11/9/2016.
 */
public class App {

    public static void main(String[] args) {

        final ModuleSystem moduleSystem = ModuleSystem.create();

        WebUtilsExporter.get().exportTo(moduleSystem);

        ComposerExporter.get().exportTo(moduleSystem);

        moduleSystem.export(SimpleEventBus.class, module -> module.export(new SimpleEventBusImpl()));

        moduleSystem.export(Db.class, module -> module.export(new MocDb()));

        DbEventHandlers.registerHandlers(moduleSystem.require(SimpleEventBus.class), moduleSystem.require(Db.class));

        moduleSystem.require(SimpleEventBus.class)
            .addInterceptorP(Cnsts.API + ".{" + EventToFlowDispatcher.ENTITY + "}.{" + EventToFlowDispatcher.ACTION + "}",
                moduleSystem.require(EventToFlowDispatcher.class)
            );

        final Vertx vertx = Vertx.vertx();

        Router router = Router.router(vertx);

//        router.get().handler(event -> event.response().end("ok"));

        router.get().handler(moduleSystem.require(RequestHandler.class));
        router.post().handler(BodyHandler.create());
        router.post().handler(moduleSystem.require(RequestHandler.class));
        router.put().handler(BodyHandler.create());
        router.put().handler(moduleSystem.require(RequestHandler.class));
        router.delete().handler(moduleSystem.require(RequestHandler.class));

        vertx.createHttpServer().requestHandler(router::accept).listen(85);
        System.out.println("Server Started.");
    }

    private static void registerEventHandlers(ModuleSystem moduleSystem) {
        SimpleEventBus eventBus = moduleSystem.require(SimpleEventBus.class);

        Flow.builder(moduleSystem.require(Flow.class, Flows.EVENT_HANDLER_FLOW))
            .replace(States.ACTION, States.CREATE)
            .handlersP(States.CREATE, o -> {
                return null;
            })
            .build()
        ;
    }
}
