package elasta.composer;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by sohan on 7/7/2017.
 */
public interface EntityToStateHandlersMap {

    Optional<StateHandlersMap> get(String entity);

    Set<String> keys();

    Map<String, StateHandlersMap> getMap();
}
