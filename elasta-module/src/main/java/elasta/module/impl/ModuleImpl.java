package elasta.module.impl;

import elasta.module.ExportScript;
import elasta.module.Module;
import elasta.module.ModuleSystem;

import java.util.Map;

/**
 * Created by Jango on 9/12/2016.
 */
final public class ModuleImpl<T> implements Module<T> {
    private final ModuleSystemImpl moduleSystem;
    private T module;

    public ModuleImpl(ModuleSystemImpl moduleSystem) {
        this.moduleSystem = moduleSystem;
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
    public <T1> T1 requireOrElse(Class<T1> t1Class, T1 defaultValue) {
        return moduleSystem.requireOrElse(t1Class, defaultValue);
    }

    @Override
    public <T1> T1 requireOrElse(Class<T1> t1Class, String moduleName, T1 defaultValue) {
        return moduleSystem.requireOrElse(t1Class, moduleName, defaultValue);
    }

    @Override
    public void export(T newModule) {
        this.module = newModule;
    }

    public T getModule() {
        return module;
    }
}
