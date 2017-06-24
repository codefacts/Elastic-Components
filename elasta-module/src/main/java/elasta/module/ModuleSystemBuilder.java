package elasta.module;

/**
 * Created by sohan on 5/14/2017.
 */
public interface ModuleSystemBuilder {

    <T> ModuleSystemBuilder export(Class<T> moduleClass, ExportScript<T> exportScript);

    <T> ModuleSystemBuilder export(Class<T> moduleClass, String moduleName, ExportScript<T> exportScript);

    ModuleSystem build();

}
