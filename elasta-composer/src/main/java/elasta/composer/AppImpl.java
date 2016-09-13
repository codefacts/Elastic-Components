package elasta.composer;

import elasta.module.ModuleSystem;
import elasta.webutils.RegisterFilters;
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

        moduleSystem.require(Vertx.class).createHttpServer().requestHandler(router::accept).listen(6500);
    }
}
