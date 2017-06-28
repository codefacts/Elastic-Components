package elasta.module;

import java.util.Optional;

/**
 * Created by sohan on 5/14/2017.
 */
@FunctionalInterface
public interface ModuleProvider {
    <T> Optional<T> get();
}
