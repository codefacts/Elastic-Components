package elasta.orm;

import elasta.module.ModuleSystem;

/**
 * Created by Jango on 9/14/2016.
 */
public interface OrmExporter {
    String DB_CONFIG = "db-config";
    String ENTITY_CLASS_MAP = "entityClassMap";
    String FUNCTION_MAP = "functionMap";
    String SYMBOL_TRANSLATOR_MAP = "symbolTranslatorMap";

    void exportTo(ModuleSystem moduleSystem);

    static OrmExporter get() {
        return OrmExporter::exportModule;
    }

    static void exportModule(ModuleSystem moduleSystem) {
    }
}
