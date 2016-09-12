package elasta.composer;

import elasta.module.ModuleSystem;
import elasta.vertxutils.VertxUtilsExporter;
import elasta.webutils.WebUtilsExporter;
import io.vertx.core.Vertx;

/**
 * Created by Jango on 9/12/2016.
 */
public class RegisterModules {

    public void register(ModuleSystem moduleSystem) {

        moduleSystem.export(Vertx.class, module -> module.export(Vertx.vertx()));

        WebUtilsExporter.get().exportModule(moduleSystem);
        VertxUtilsExporter.get().export(moduleSystem);

        new RegisterEventHandlersImpl().register(moduleSystem);
    }
}
