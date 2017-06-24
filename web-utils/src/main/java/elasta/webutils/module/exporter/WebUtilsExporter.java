package elasta.webutils.module.exporter;

import elasta.module.MutableModuleSystem;

/**
 * Created by Jango on 11/9/2016.
 */
public interface WebUtilsExporter {
    void exportTo(MutableModuleSystem mutableModuleSystem);

    static WebUtilsExporter get() {
        return new WebUtilsExporterImpl();
    }
}
