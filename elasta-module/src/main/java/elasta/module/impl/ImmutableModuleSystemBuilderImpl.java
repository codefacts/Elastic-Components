package elasta.module.impl;

import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.module.ExportScript;
import elasta.module.ImmutableModuleSystem;
import elasta.module.ImmutableModuleSystemBuilder;
import elasta.module.ModuleProvider;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sohan on 5/14/2017.
 */
final public class ImmutableModuleSystemBuilderImpl implements ImmutableModuleSystemBuilder {
    final Map<ImmutableModuleSystemImpl.TypeAndNamePair, ModuleProvider> typeAndNamePairToModuleHolderMap = new LinkedHashMap<>();
    final ModuleMapProxy moduleMapProxy = new ModuleMapProxy();

    @Override
    public <T> ImmutableModuleSystemBuilderImpl export(Class<T> moduleClass, ExportScript<T> exportScript) {
        put(
            ImmutableModuleSystemImpl.TypeAndNamePair.builder().type(moduleClass).build(),
            exportScript
        );
        return this;
    }

    @Override
    public <T> ImmutableModuleSystemBuilderImpl export(Class<T> moduleClass, String moduleName, ExportScript<T> exportScript) {
        if (Utils.not(typeAndNamePairToModuleHolderMap.containsKey(
            ImmutableModuleSystemImpl.TypeAndNamePair.builder().type(moduleClass).build()
        ))) {
            put(
                ImmutableModuleSystemImpl.TypeAndNamePair.builder()
                    .type(moduleClass)
                    .build(),
                exportScript
            );
        }
        put(
            ImmutableModuleSystemImpl.TypeAndNamePair.builder()
                .type(moduleClass)
                .name(moduleName)
                .build(),
            exportScript
        );
        return this;
    }

    @Override
    public ImmutableModuleSystem build() {
        ImmutableMap<ImmutableModuleSystemImpl.TypeAndNamePair, ModuleProvider> map = ImmutableMap.copyOf(typeAndNamePairToModuleHolderMap);
        return createImmutableModuleSystem(map);
    }

    private ImmutableModuleSystem createImmutableModuleSystem(ImmutableMap<ImmutableModuleSystemImpl.TypeAndNamePair, ModuleProvider> map) {
        moduleMapProxy.setMap(map);
        return new ImmutableModuleSystemImpl(
            map
        );
    }

    private <T> void put(ImmutableModuleSystemImpl.TypeAndNamePair typeAndNamePair, ExportScript<T> exportScript) {
        typeAndNamePairToModuleHolderMap.put(
            typeAndNamePair,
            new ModuleProviderImpl(exportScript, moduleMapProxy)
        );
    }
}
