package elasta.composer.module_exporter;

import elasta.module.ModuleSystem;

/**
 * Created by Jango on 11/13/2016.
 */
public interface ComposerExporter {
    void export(ModuleSystem moduleSystem);

    static ComposerExporter get() {
        return new ComposerExporterImpl();
    }
}
