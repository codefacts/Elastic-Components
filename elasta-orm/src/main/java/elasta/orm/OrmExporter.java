package elasta.orm;

import elasta.module.ModuleSystem;

/**
 * Created by Jango on 9/14/2016.
 */
public interface OrmExporter {

    void exportTo(ModuleSystem moduleSystem);

    static OrmExporter get() {
        return OrmExporter::exportModule;
    }

    static void exportModule(ModuleSystem moduleSystem) {
    }
}
