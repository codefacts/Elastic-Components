package elasta.module;

/**
 * Created by Jango on 9/12/2016.
 */
public interface Module<TT> {

    <T> T require(Class<T> tClass);

    <T> T require(Class<T> tClass, String moduleName);

    void export(TT newModule);
}
