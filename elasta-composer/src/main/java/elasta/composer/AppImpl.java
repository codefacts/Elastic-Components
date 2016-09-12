package elasta.composer;

import elasta.module.ModuleSystem;
import elasta.vertxutils.VertxUtils;
import elasta.webutils.RouteUtils;
import elasta.webutils.WebUtils;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Created by Jango on 9/11/2016.
 */
public class AppImpl implements App {

    public static void main(String[] args) {

        ModuleSystem moduleSystem = ModuleSystem.create();

        new RegisterModules().register(moduleSystem);

        Router router = Router.router(moduleSystem.require(Vertx.class));

        registerFilters(router);

        RouteUtils routeUtils = new RouteUtils(
            (s, httpMethod) -> BodyHandler.create(),
            RouteUtils.defaultHandlerFactory(
                moduleSystem.require(VertxUtils.class),
                moduleSystem.require(WebUtils.class)
            )
        );

        routeUtils.registerRoutesTo(router, routeUtils.createRoutes("/api", "users"));



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
