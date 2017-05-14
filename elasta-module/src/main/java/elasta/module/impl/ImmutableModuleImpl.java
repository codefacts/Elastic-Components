package elasta.module.impl;

import elasta.module.ImmutableModuleSystem;
import elasta.module.Module;
import elasta.module.ModuleProvider;
import elasta.module.ex.ModuleSystemException;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 5/14/2017.
 */
final public class ImmutableModuleImpl<TT> implements Module<TT> {
    final ImmutableModuleSystem immutableModuleSystem;
    TT exportedModule;

    public ImmutableModuleImpl(Map<ImmutableModuleSystemImpl.TypeAndNamePair, ModuleProvider> typeAndNamePairToModuleHolderMap) {
        Objects.requireNonNull(typeAndNamePairToModuleHolderMap);
        immutableModuleSystem = new ImmutableModuleSystemImpl(
            typeAndNamePairToModuleHolderMap
        );
    }

    @Override
    public <T> T require(Class<T> tClass) {
        return immutableModuleSystem.require(tClass);
    }

    @Override
    public <T> T require(Class<T> tClass, String moduleName) {
        return immutableModuleSystem.require(tClass, moduleName);
    }

    @Override
    public <T> T requireOrElse(Class<T> tClass, T defaultValue) {
        return immutableModuleSystem.requireOrElse(tClass, defaultValue);
    }

    @Override
    public <T> T requireOrElse(Class<T> tClass, String moduleName, T defaultValue) {
        return immutableModuleSystem.requireOrElse(tClass, moduleName, defaultValue);
    }

    @Override
    public void export(TT newModule) {
        this.exportedModule = newModule;
    }

    public Optional<TT> getExportedModule() {
        return Optional.ofNullable(exportedModule);
    }
}
