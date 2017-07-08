package elasta.composer.impl;

import com.google.common.collect.ImmutableMap;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.StateHandlersMap;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Created by sohan on 7/7/2017.
 */
final public class StateHandlersMapImpl implements StateHandlersMap {
    final Map<Class, MsgEnterEventHandlerP> map;

    public StateHandlersMapImpl(Map<Class, MsgEnterEventHandlerP> map) {
        Objects.requireNonNull(map);
        this.map = ImmutableMap.copyOf(map);
    }

    @Override
    public <T extends MsgEnterEventHandlerP<P, R>, P, R> Optional<T> get(Class<T> tClass) {
        return Optional.ofNullable(
            (T) map.get(tClass)
        );
    }

    @Override
    public Set<Class> keys() {
        return map.keySet();
    }

    @Override
    public Map<Class, MsgEnterEventHandlerP> getMap() {
        return map;
    }
}
