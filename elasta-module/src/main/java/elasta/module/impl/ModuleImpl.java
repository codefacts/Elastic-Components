package elasta.module.impl;

import elasta.module.ExportScript;
import elasta.module.Module;

import java.util.Map;

/**
 * Created by Jango on 9/12/2016.
 */
public class ModuleImpl<T> implements Module<T> {
    private final ModuleSystemImpl moduleSystem;
    private final String moduleName;
    private ModuleSpec moduleSpec;
    private T module;

    public ModuleImpl(ModuleSystemImpl moduleSystem) {
        this(moduleSystem, null);
    }

    public ModuleImpl(ModuleSystemImpl moduleSystem, String moduleName) {
        this.moduleSystem = moduleSystem;
        this.moduleName = moduleName;
    }

    @Override
    public <TT> TT require(Class<TT> ttClass) {
        return moduleSystem.require(ttClass);
    }

    @Override
    public <TT> TT require(Class<TT> ttClass, String moduleName) {
        return moduleSystem.require(ttClass, moduleName);
    }

    @Override
    public void export(T newModule) {
        this.moduleSpec = new ModuleSpec(newModule.getClass(), moduleName);
        this.module = newModule;
    }

    public ModuleSpec getModuleSpec() {
        return moduleSpec;
    }

    public T getModule() {
        return module;
    }
}
