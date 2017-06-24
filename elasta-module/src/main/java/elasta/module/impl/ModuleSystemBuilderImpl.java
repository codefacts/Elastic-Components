package elasta.module.impl;

import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.module.ExportScript;
import elasta.module.ModuleSystem;
import elasta.module.ModuleSystemBuilder;
import elasta.module.ModuleProvider;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sohan on 5/14/2017.
 */
final public class ModuleSystemBuilderImpl implements ModuleSystemBuilder {
    final Map<ModuleSystemImpl.TypeAndNamePair, ModuleProvider> typeAndNamePairToModuleHolderMap = new LinkedHashMap<>();
    final ModuleMapProxy moduleMapProxy = new ModuleMapProxy();

    @Override
    public <T> ModuleSystemBuilderImpl export(Class<T> moduleClass, ExportScript<T> exportScript) {
        put(
            ModuleSystemImpl.TypeAndNamePair.builder().type(moduleClass).build(),
            exportScript
        );
        return this;
    }

    @Override
    public <T> ModuleSystemBuilderImpl export(Class<T> moduleClass, String moduleName, ExportScript<T> exportScript) {
        if (Utils.not(typeAndNamePairToModuleHolderMap.containsKey(
            ModuleSystemImpl.TypeAndNamePair.builder().type(moduleClass).build()
        ))) {
            put(
                ModuleSystemImpl.TypeAndNamePair.builder()
                    .type(moduleClass)
                    .build(),
                exportScript
            );
        }
        put(
            ModuleSystemImpl.TypeAndNamePair.builder()
                .type(moduleClass)
                .name(moduleName)
                .build(),
            exportScript
        );
        return this;
    }

    @Override
    public ModuleSystem build() {
        ImmutableMap<ModuleSystemImpl.TypeAndNamePair, ModuleProvider> map = ImmutableMap.copyOf(typeAndNamePairToModuleHolderMap);
        return createImmutableModuleSystem(map);
    }

    private ModuleSystem createImmutableModuleSystem(ImmutableMap<ModuleSystemImpl.TypeAndNamePair, ModuleProvider> map) {
        moduleMapProxy.setMap(map);
        return new ModuleSystemImpl(
            map
        );
    }

    private <T> void put(ModuleSystemImpl.TypeAndNamePair typeAndNamePair, ExportScript<T> exportScript) {
        typeAndNamePairToModuleHolderMap.put(
            typeAndNamePair,
            new ModuleProviderImpl(exportScript, moduleMapProxy)
        );
    }
}
