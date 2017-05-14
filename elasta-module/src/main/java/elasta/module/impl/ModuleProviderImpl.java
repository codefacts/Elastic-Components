package elasta.module.impl;

import elasta.module.ExportScript;
import elasta.module.ModuleProvider;
import elasta.module.ex.ModuleSystemException;

import java.util.Objects;

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
    public <T> T get() {

        if (exportedModule == null) {

            return cast(exportedModule = executeScript());
        }

        return cast(exportedModule);
    }

    private <T> T cast(Object exportedModule) {
        return (T) exportedModule;
    }

    private <T> T executeScript() {
        final ImmutableModuleImpl<T> immutableModule = new ImmutableModuleImpl<>(moduleMapProxy.getMap());

        exportScript.run(immutableModule);

        return immutableModule.getExportedModule()
            .orElseThrow(() -> new ModuleSystemException("No module was exported during export script execution"));
    }
}
