package elasta.orm.event.builder;

import elasta.orm.event.EventDispatcher;

/**
 * Created by sohan on 3/28/2017.
 */
public interface BuilderContext<T> {

    BuilderContext<T> putEmpty(String key);

    boolean isEmpty(String key);

    boolean contains(String key);

    T get(String key);

    BuilderContext<T> put(String entity, T upsertEventDispatcher);
}
