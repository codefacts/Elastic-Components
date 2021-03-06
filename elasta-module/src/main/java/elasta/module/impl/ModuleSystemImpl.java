package elasta.module.impl;

import elasta.module.ModuleSystem;
import elasta.module.ModuleProvider;
import elasta.module.ex.ModuleSystemException;
import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 5/14/2017.
 */
final public class ModuleSystemImpl implements ModuleSystem {
    final Map<TypeAndNamePair, ModuleProvider> typeAndNamePairToModuleHolderMap;

    public ModuleSystemImpl(Map<TypeAndNamePair, ModuleProvider> typeAndNamePairToModuleHolderMap) {
        Objects.requireNonNull(typeAndNamePairToModuleHolderMap);
        this.typeAndNamePairToModuleHolderMap = typeAndNamePairToModuleHolderMap;
    }

    @Override
    public <T> T require(Class<T> type) {
        Objects.requireNonNull(type);
        return this.<T>get(
            TypeAndNamePair.builder()
                .type(type)
                .build()
        ).orElseThrow(() -> new ModuleSystemException("No module found for type '" + type + "'"));
    }

    @Override
    public <T> T require(Class<T> type, String moduleName) {
        Objects.requireNonNull(type);
        return this.<T>get(
            TypeAndNamePair.builder()
                .type(type)
                .name(moduleName)
                .build()
        ).orElseThrow(() -> new ModuleSystemException("No module found for type '" + type + "' and name '" + moduleName + "'"));
    }

    @Override
    public <T> T requireOrElse(Class<T> type, T defaultValue) {
        Objects.requireNonNull(type);
        return this.<T>get(
            TypeAndNamePair.builder()
                .type(type)
                .build()
        ).orElse(defaultValue);
    }

    @Override
    public <T> T requireOrElse(Class<T> type, String moduleName, T defaultValue) {
        Objects.requireNonNull(type);
        return this.<T>get(
            TypeAndNamePair.builder()
                .type(type)
                .name(moduleName)
                .build()
        ).orElse(defaultValue);
    }

    private <T> Optional<T> get(TypeAndNamePair typeAndNamePair) {
        Objects.requireNonNull(typeAndNamePair);

        return Optional.ofNullable(typeAndNamePairToModuleHolderMap.get(typeAndNamePair))
            .map(
                moduleProvider -> moduleProvider.<T>get()
                    .orElseThrow(() -> new ModuleSystemException("No module was exported during script execution for module '" + typeAndNamePair + "'"))
            );
    }

    @Value
    @Builder
    final static class TypeAndNamePair {
        final Class type;
        final String name;

        public TypeAndNamePair(Class type, String name) {
            Objects.requireNonNull(type);
            this.type = type;
            this.name = (name == null) ? null : name;
        }

        public Optional<String> getName() {
            return Optional.ofNullable(name);
        }

        @Override
        public String toString() {
            return "(type=" + type + getName().map(nm -> ", name=" + nm).orElse("") + ")";
        }
    }

}
