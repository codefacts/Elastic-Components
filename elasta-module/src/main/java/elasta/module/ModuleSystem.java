package elasta.module;

import elasta.module.impl.ModuleSystemImpl;

/**
 * Created by Jango on 9/12/2016.
 */
public interface ModuleSystem {

    <T> T require(Class<T> tClass);

    <T> T require(Class<T> tClass, String moduleName);

    <T> T requireOrElse(Class<T> tClass, T defaultValue);

    <T> T requireOrElse(Class<T> tClass, String moduleName, T defaultValue);

    <T> void export(Class<T> moduleClass, ExportScript<T> exportScript);

    <T> void export(Class<T> moduleClass, String moduleName, ExportScript<T> exportScript);

    <T> void exportPrototype(Class<T> moduleClass, ExportScript<T> exportScript);

    <T> void exportPrototype(Class<T> moduleClass, String moduleName, ExportScript<T> exportScript);

    static ModuleSystem create() {
        return new ModuleSystemImpl();
    }
}
