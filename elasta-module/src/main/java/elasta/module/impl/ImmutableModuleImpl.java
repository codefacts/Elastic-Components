package elasta.module.impl;

import elasta.module.ModuleSystem;
import elasta.module.Module;
import elasta.module.ModuleProvider;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 5/14/2017.
 */
final public class ImmutableModuleImpl<TT> implements Module<TT> {
    final ModuleSystem moduleSystem;
    TT exportedModule;

    public ImmutableModuleImpl(Map<ModuleSystemImpl.TypeAndNamePair, ModuleProvider> typeAndNamePairToModuleHolderMap) {
        Objects.requireNonNull(typeAndNamePairToModuleHolderMap);
        moduleSystem = new ModuleSystemImpl(
            typeAndNamePairToModuleHolderMap
        );
    }

    @Override
    public <T> T require(Class<T> tClass) {
        return moduleSystem.require(tClass);
    }

    @Override
    public <T> T require(Class<T> tClass, String moduleName) {
        return moduleSystem.require(tClass, moduleName);
    }

    @Override
    public <T> T requireOrElse(Class<T> tClass, T defaultValue) {
        return moduleSystem.requireOrElse(tClass, defaultValue);
    }

    @Override
    public <T> T requireOrElse(Class<T> tClass, String moduleName, T defaultValue) {
        return moduleSystem.requireOrElse(tClass, moduleName, defaultValue);
    }

    @Override
    public void export(TT newModule) {
        this.exportedModule = newModule;
    }

    public Optional<TT> getExportedModule() {
        return Optional.ofNullable(exportedModule);
    }
}
