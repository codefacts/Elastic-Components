package elasta.composer;

import elasta.module.ModuleSystem;
import elasta.webutils.EventHandlerGenerator;
import elasta.webutils.RouteGenerators;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 * Created by Jango on 9/11/2016.
 */
public class AppImpl implements App {

    public static void main(String[] args) {

        ModuleSystem moduleSystem = ModuleSystem.create();

        new RegisterModules().register(moduleSystem);

        Router router = Router.router(moduleSystem.require(Vertx.class));

        registerFilters(router);

        RouteGenerators routeGenerators = moduleSystem.require(RouteGenerators.class);

        routeGenerators.registerRoutes(router, routeGenerators.makeRoutes("/api", "users"));

        EventHandlerGenerator eventHandlerGenerator = moduleSystem.require(EventHandlerGenerator.class);

        eventHandlerGenerator.registerHandlers(eventHandlerGenerator.makeHandlers("users"));

        moduleSystem.require(Vertx.class).createHttpServer().requestHandler(router::accept).listen(6500);
        System.out.println("started");
    }

    private static void registerFilters(Router router) {
        router.route("/api/*").handler(event -> {
            event.response().putHeader("Content-Type", "application/json");
            event.next();
        });
    }
}
