package elasta.webutils.module.exporter;

import elasta.module.ModuleSystem;

/**
 * Created by Jango on 11/9/2016.
 */
public interface WebUtilsExporter {
    void exportTo(ModuleSystem moduleSystem);

    static WebUtilsExporter get() {
        return new WebUtilsExporterImpl();
    }
}
