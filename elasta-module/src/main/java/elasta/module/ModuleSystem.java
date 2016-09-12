package elasta.module;

import elasta.module.impl.ModuleSystemImpl;

/**
 * Created by Jango on 9/12/2016.
 */
public interface ModuleSystem {

    <T> T require(Class<T> tClass);

    <T> T require(Class<T> tClass, String moduleName);

    <T> void export(ExportScript<T> exportScript, Class<T> moduleClass);

    <T> void export(ExportScript<T> exportScript, Class<T> moduleClass, String moduleName);

    static ModuleSystem create() {
        return new ModuleSystemImpl();
    }
}
