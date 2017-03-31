package elasta.orm.event.builder.impl;

import elasta.commons.Utils;
import elasta.orm.event.builder.BuilderContext;
import elasta.orm.event.builder.ex.BuilderContextException;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static elasta.commons.Utils.not;

/**
 * Created by sohan on 3/28/2017.
 */
final public class BuilderContextImpl<T> implements BuilderContext<T> {
    final Map<String, Optional<T>> map;

    public BuilderContextImpl(Map<String, Optional<T>> map) {
        Objects.requireNonNull(map);
        this.map = map;
    }

    @Override
    public BuilderContext<T> putEmpty(String key) {
        map.put(key, Optional.empty());
        return this;
    }

    @Override
    public boolean isEmpty(String key) {
        return map.containsKey(key) && not(map.get(key).isPresent());
    }

    @Override
    public boolean contains(String key) {
        return map.containsKey(key) && map.get(key).isPresent();
    }

    @Override
    public T get(String key) {
        Optional<T> optional = map.get(key);
        if (optional == null) {
            throw new BuilderContextException("No object found for key '" + key + "'");
        }
        return optional.get();
    }

    @Override
    public BuilderContext<T> put(String entity, T upsertEventDispatcher) {
        map.put(entity, Optional.of(upsertEventDispatcher));
        return this;
    }
}
