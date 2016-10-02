package elasta.orm;

import elasta.module.ModuleSystem;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * Created by Jango on 9/14/2016.
 */
public interface OrmExporter {
    String DB_CONFIG = "db-config";

    void export(ModuleSystem moduleSystem);

    static OrmExporter get() {
        return moduleSystem -> {

            moduleSystem.export(JDBCClient.class,
                module -> module.export(JDBCClient.createShared(
                    module.require(Vertx.class), module.require(JsonObject.class, DB_CONFIG))
                ));

        };
    }
}
