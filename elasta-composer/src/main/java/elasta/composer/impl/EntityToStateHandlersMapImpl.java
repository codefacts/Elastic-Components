package elasta.composer.impl;

import com.google.common.collect.ImmutableMap;
import elasta.composer.EntityToStateHandlersMap;
import elasta.composer.StateHandlersMap;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Created by sohan on 7/7/2017.
 */
final public class EntityToStateHandlersMapImpl implements EntityToStateHandlersMap {
    final Map<String, StateHandlersMap> map;

    public EntityToStateHandlersMapImpl(Map<String, StateHandlersMap> entityToStateHandlersMap) {
        Objects.requireNonNull(entityToStateHandlersMap);
        this.map = ImmutableMap.copyOf(entityToStateHandlersMap);
    }

    @Override
    public Optional<StateHandlersMap> get(String entity) {

        return Optional.ofNullable(
            map.get(entity)
        );
    }

    @Override
    public Set<String> keys() {
        return map.keySet();
    }

    @Override
    public Map<String, StateHandlersMap> getMap() {
        return map;
    }
}
