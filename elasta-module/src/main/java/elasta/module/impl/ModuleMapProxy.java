package elasta.module.impl;

import elasta.module.ModuleProvider;
import elasta.module.ex.ModuleMapProxyException;

import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 5/14/2017.
 */
final class ModuleMapProxy {
    Map<ImmutableModuleSystemImpl.TypeAndNamePair, ModuleProvider> typeAndNamePairToModuleHolderMap;

    ModuleMapProxy setMap(Map<ImmutableModuleSystemImpl.TypeAndNamePair, ModuleProvider> typeAndNamePairToModuleHolderMap) {
        Objects.requireNonNull(typeAndNamePairToModuleHolderMap);
        this.typeAndNamePairToModuleHolderMap = typeAndNamePairToModuleHolderMap;
        return this;
    }

    Map<ImmutableModuleSystemImpl.TypeAndNamePair, ModuleProvider> getMap() {
        if (typeAndNamePairToModuleHolderMap == null) {
            throw new ModuleMapProxyException("No ModuleMap is set yet for ModuleMapProxy");
        }
        return typeAndNamePairToModuleHolderMap;
    }
}
