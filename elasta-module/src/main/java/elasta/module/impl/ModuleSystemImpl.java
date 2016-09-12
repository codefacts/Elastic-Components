package elasta.module.impl;

import elasta.module.ExportScript;
import elasta.module.Module;
import elasta.module.ModuleSystem;

import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Created by Jango on 9/12/2016.
 */
public class ModuleSystemImpl implements ModuleSystem {

    private final Map<ModuleSpec, ExportScript> scriptMap = new HashMap<>();
    private final Map<ModuleSpec, Object> moduleMap = new HashMap<>();

    @Override
    public <T> T require(Class<T> tClass) {
        return require(tClass, null);
    }

    @Override
    public <T> T require(Class<T> tClass, String moduleName) {
        return getOrCreate(tClass, moduleName);
    }

    private <T> T getOrCreate(Class<T> moduleClass, String moduleName) {

        final ModuleSpec moduleSpec = new ModuleSpec(moduleClass, moduleName);

        Object module = moduleMap.get(moduleSpec);

        if (module == null) {

            ExportScript exportScript = scriptMap.get(moduleSpec);

            if (exportScript == null) {

                if (moduleName == null) {

                    ModuleSpec spec = scriptMap.keySet().stream()
                        .filter(ms -> ms.moduleClass == moduleClass)
                        .findFirst()
                        .orElseThrow(() -> new ModuleSystemException("No module is found for type: '" + moduleClass.getName() + "'"));

                    exportScript = scriptMap.get(spec);

                } else {
                    throw new ModuleSystemException("No module is found for type: '" + moduleClass.getName() + "' and name: '" + moduleName + "'");
                }
            }

            module = createModule(exportScript, moduleName);

            ensureModuleExportedOrThrow(module, moduleClass, moduleName);

            moduleMap.put(new ModuleSpec(moduleClass, moduleName), module);

            //Check Default Exists or register this module as default
            moduleMap.putIfAbsent(new ModuleSpec(moduleClass, null), module);

        }
        return (T) module;
    }

    @Override
    public <T> void export(ExportScript<T> exportScript, Class<T> moduleClass) {
        export(exportScript, moduleClass, null);
    }

    @Override
    public <T> void export(ExportScript<T> exportScript, Class<T> moduleClass, String moduleName) {
        scriptMap.put(new ModuleSpec(moduleClass, moduleName), exportScript);
    }

    private <T> T createModule(ExportScript<T> exportScript, String moduleName) {

        ModuleImpl<T> module = new ModuleImpl<>(this, moduleName);

        exportScript.run(module);

        T newModule = module.getModule();

        return newModule;

    }

    private <T> void ensureModuleExportedOrThrow(Object module, Class<T> tClass, String moduleName) {
        if (module == null) {
            throw new ModuleSystemException("Module is not exported after script execution for type: '" + tClass + "'" +
                (moduleName == null ? "" : " and moduleName: '" + moduleName + "'"));
        }
    }

    public static void main(String[] args) {

    }
}
