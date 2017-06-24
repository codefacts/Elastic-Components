package elasta.module;

import elasta.module.impl.MutableModuleSystemImpl;

/**
 * Created by Jango on 9/12/2016.
 */
public interface MutableModuleSystem extends ModuleSystem {

    <T> MutableModuleSystem export(Class<T> moduleClass, ExportScript<T> exportScript);

    <T> MutableModuleSystem export(Class<T> moduleClass, String moduleName, ExportScript<T> exportScript);

    static MutableModuleSystem create() {
        return new MutableModuleSystemImpl();
    }
}
