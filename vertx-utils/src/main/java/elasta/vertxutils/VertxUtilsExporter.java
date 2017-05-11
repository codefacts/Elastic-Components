package elasta.vertxutils;

import elasta.module.ModuleSystem;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;

/**
 * Created by Jango on 9/12/2016.
 */
public interface VertxUtilsExporter {

    void export(ModuleSystem moduleSystem);

    static VertxUtilsExporter get() {
        return moduleSystem -> {
        };
    }

    public static void main(String[] args) {
        System.out.println("ok");
    }
}
