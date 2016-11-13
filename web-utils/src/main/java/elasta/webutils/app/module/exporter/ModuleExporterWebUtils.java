package elasta.webutils.app.module.exporter;

import elasta.module.ModuleSystem;

/**
 * Created by Jango on 11/9/2016.
 */
public interface ModuleExporterWebUtils {
    void exportModule(ModuleSystem moduleSystem);

    static ModuleExporterWebUtils get() {
        return new ModuleExporterImpl();
    }
}
