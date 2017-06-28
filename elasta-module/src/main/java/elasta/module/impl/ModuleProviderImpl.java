package elasta.module.impl;

import elasta.module.ExportScript;
import elasta.module.ModuleProvider;
import elasta.module.ex.ModuleSystemException;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 5/14/2017.
 */
final public class ModuleProviderImpl implements ModuleProvider {
    final ExportScript exportScript;
    final ModuleMapProxy moduleMapProxy;
    private Object exportedModule;

    public ModuleProviderImpl(ExportScript exportScript, ModuleMapProxy moduleMapProxy) {
        Objects.requireNonNull(exportScript);
        Objects.requireNonNull(moduleMapProxy);
        this.exportScript = exportScript;
        this.moduleMapProxy = moduleMapProxy;
    }

    @Override
    public <T> Optional<T> get() {

        if (exportedModule == null) {

            executeScript().ifPresent(newModule -> exportedModule = newModule);
        }

        return Optional.ofNullable((T) exportedModule);
    }

    private <T> Optional<T> executeScript() {

        final ImmutableModuleImpl<T> immutableModule = new ImmutableModuleImpl<>(moduleMapProxy.getMap());

        exportScript.run(immutableModule);

        return immutableModule.getExportedModule();
    }
}
