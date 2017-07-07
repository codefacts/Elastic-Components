package tracker.entity_config;

import com.google.common.collect.ImmutableList;
import elasta.orm.entity.core.Field;

import java.util.Collection;
import java.util.List;

/**
 * Created by sohan on 7/7/2017.
 */
public interface ArrayBuilder<T> {

    ArrayBuilder<T> add(T field);

    ArrayBuilder<T> addAll(Collection<T> fields);

    T[] build(CreateArrayFunc<T> createArrayFunc);

    interface CreateArrayFunc<T> {
        T[] apply(List<T> list);
    }

    static <T> ArrayBuilder<T> create() {
        return new ArrayBuilderImpl<>();
    }
}
