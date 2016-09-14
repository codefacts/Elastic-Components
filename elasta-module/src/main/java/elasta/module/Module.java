package elasta.module;

/**
 * Created by Jango on 9/12/2016.
 */
public interface Module<TT> {

    <T> T require(Class<T> tClass);

    <T> T require(Class<T> tClass, String moduleName);

    <T> T requireOrElse(Class<T> tClass, T defaultValue);

    <T> T requireOrElse(Class<T> tClass, String moduleName, T defaultValue);

    void export(TT newModule);

    ModuleSystem moduleSystem();
}
