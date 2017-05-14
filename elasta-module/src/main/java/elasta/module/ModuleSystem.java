package elasta.module;

import elasta.module.impl.ModuleSystemImpl;

/**
 * Created by Jango on 9/12/2016.
 */
public interface ModuleSystem extends ImmutableModuleSystem {

    <T> ModuleSystem export(Class<T> moduleClass, ExportScript<T> exportScript);

    <T> ModuleSystem export(Class<T> moduleClass, String moduleName, ExportScript<T> exportScript);

    static ModuleSystem create() {
        return new ModuleSystemImpl();
    }
}
