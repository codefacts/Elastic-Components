package elasta.module;

import elasta.module.impl.ModuleSystemImpl;

/**
 * Created by sohan on 5/14/2017.
 */
public interface ImmutableModuleSystemBuilder {

    <T> ImmutableModuleSystemBuilder export(Class<T> moduleClass, ExportScript<T> exportScript);

    <T> ImmutableModuleSystemBuilder export(Class<T> moduleClass, String moduleName, ExportScript<T> exportScript);

    ImmutableModuleSystem build();

}
