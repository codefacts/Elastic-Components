package elasta.module;

/**
 * Created by sohan on 5/14/2017.
 */
@FunctionalInterface
public interface ModuleProvider {
    <T> T get();
}
