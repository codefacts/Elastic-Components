package elasta.module;

import elasta.module.impl.ImmutableModuleSystemBuilderImpl;

/**
 * Created by sohan on 5/14/2017.
 */
public interface ImmutableModuleSystem {

    <T> T require(Class<T> tClass);

    <T> T require(Class<T> tClass, String moduleName);

    <T> T requireOrElse(Class<T> tClass, T defaultValue);

    <T> T requireOrElse(Class<T> tClass, String moduleName, T defaultValue);

    static ImmutableModuleSystemBuilder builder() {
        return new ImmutableModuleSystemBuilderImpl();
    }
}
