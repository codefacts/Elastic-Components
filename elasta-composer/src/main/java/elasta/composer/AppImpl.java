package elasta.composer;

import elasta.module.ModuleSystem;
import elasta.webutils.CrudBuilder;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 * Created by Jango on 9/11/2016.
 */
public class AppImpl {

    public static void main(String[] args) {

        ModuleSystem moduleSystem = ModuleSystem.create();

        new RegisterModules().register(moduleSystem);

        Router router = Router.router(moduleSystem.require(Vertx.class));

        CrudBuilder crudBuilder = moduleSystem.require(CrudBuilder.class);

        crudBuilder.addRoutesAndHandlers(router, "/api", "users");

        crudBuilder.addRoutesAndHandlers(router, "/api", "books");

        crudBuilder.addRoutesAndHandlers(router, "/api", "taxes");

        crudBuilder.addRoutesAndHandlers(router, "/api", "tokens");

        moduleSystem.require(Vertx.class).createHttpServer().requestHandler(router::accept).listen(6500);

        System.out.println("started");
    }
}
