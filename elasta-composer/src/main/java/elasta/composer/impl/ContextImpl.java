package elasta.composer.impl;

import com.google.common.collect.ImmutableMap;
import elasta.composer.Context;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 5/15/2017.
 */
final public class ContextImpl implements Context {
    final Map<String, Object> map;

    public ContextImpl(Map<String, Object> map) {
        Objects.requireNonNull(map);
        this.map = map;
    }

    @Override
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    @Override
    public <T> Optional<T> get(String key) {
        return Optional.ofNullable(cast(map.get(key)));
    }

    @Override
    public Map<String, Object> getMap() {
        return map;
    }

    @Override
    public Context addAll(Map<String, Object> map) {
        return new ContextImpl(
            ImmutableMap.<String, Object>builder().putAll(map).build()
        );
    }

    private <T> T cast(Object value) {
        return (T) value;
    }
}
