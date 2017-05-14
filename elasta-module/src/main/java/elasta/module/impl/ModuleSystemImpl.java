package elasta.module.impl;

import elasta.module.ExportScript;
import elasta.module.ModuleSystem;
import elasta.module.ex.ModuleSystemException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jango on 9/12/2016.
 */
final public class ModuleSystemImpl implements ModuleSystem {

    private static final boolean DEFALUT_PROTOTYPE = false;
    private final Map<ModuleSpec, ModuleInfo> scriptMap = new HashMap<>();
    private final Map<ModuleSpec, Object> moduleMap = new HashMap<>();

    @Override
    public <T> T require(Class<T> tClass) {
        return require(tClass, (String) null);
    }

    @Override
    public <T> T require(Class<T> tClass, String moduleName) {
        T module = getOrCreate(tClass, moduleName);

        if (module == null) {

            moduleName = moduleName == null ? "" : (" and name: '" + moduleName + "'");

            throw new ModuleSystemException("No module is found for type: '" + tClass + "'" + moduleName);
        }

        return module;
    }

    @Override
    public <T> T requireOrElse(Class<T> tClass, T defaultValue) {
        return requireOrElse(tClass, null, defaultValue);
    }

    @Override
    public <T> T requireOrElse(Class<T> tClass, String moduleName, T defaultValue) {
        T module = getOrCreate(tClass, moduleName);
        return module == null ? defaultValue : module;
    }

    private <T> T getOrCreate(Class<T> moduleClass, String moduleName) {

        final ModuleSpec moduleSpec = new ModuleSpec(moduleClass, moduleName);

        ModuleInfo moduleInfo = scriptMap.get(moduleSpec);

        if (moduleInfo == null) {

            if (moduleName == null) {

                ModuleSpec spec = scriptMap.keySet().stream()
                    .filter(ms -> ms.moduleClass == moduleClass)
                    .findFirst()
                    .orElseThrow(() -> new ModuleSystemException("Module is not found for type: '" + moduleClass + "'"));

                if (spec == null) {
                    return null;
                }

                moduleInfo = scriptMap.get(spec);

            } else {
                return null;
            }

        }

        if (moduleInfo.isPrototype) {
            return (T) createModule(moduleInfo.exportScript);
        } else {
            return findModule(moduleSpec, moduleInfo);
        }
    }

    private <T> T findModule(ModuleSpec moduleSpec, ModuleInfo moduleInfo) {

        T module = (T) moduleMap.get(moduleSpec);
        Class moduleClass = moduleSpec.moduleClass;
        String moduleName = moduleSpec.moduleName;

        if (module == null) {

            module = (T) createModule(moduleInfo.exportScript);

            ensureModuleExportedOrThrow(module, moduleClass, moduleName);

            moduleMap.put(moduleSpec, module);

            //Check Default Exists or register this module as default
            moduleMap.putIfAbsent(new ModuleSpec(moduleClass, null), module);

        }
        return (T) module;
    }

    @Override
    public <T> ModuleSystemImpl export(Class<T> moduleClass, ExportScript<T> exportScript) {
        export(moduleClass, null, exportScript);
        return this;
    }

    @Override
    public <T> ModuleSystemImpl export(Class<T> moduleClass, String moduleName, ExportScript<T> exportScript) {
        scriptMap.put(new ModuleSpec(moduleClass, moduleName), new ModuleInfo<>(exportScript, DEFALUT_PROTOTYPE));
        return this;
    }

    private <T> T createModule(ExportScript<T> exportScript) {

        ModuleImpl<T> module = new ModuleImpl<>(this);

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
