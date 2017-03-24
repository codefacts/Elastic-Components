package elasta.orm.delete.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.orm.delete.TableToTableDependenciesMap;
import elasta.orm.delete.ex.TableToTableDependenciesMapException;
import elasta.orm.delete.loader.impl.DependencyInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Created by sohan on 3/13/2017.
 */
final public class TableToTableDependenciesMapImpl implements TableToTableDependenciesMap {
    final Map<String, List<DependencyInfo>> map;

    public TableToTableDependenciesMapImpl(Map<String, List<DependencyInfo>> map) {
        Objects.requireNonNull(map);
        this.map = toImmutable(map);
    }

    @Override
    public List<DependencyInfo> get(String table) {
        List<DependencyInfo> dependencyInfos = map.get(table);
        return dependencyInfos == null ? ImmutableList.of() : dependencyInfos;
    }

    @Override
    public void forEach(BiConsumer<String, List<DependencyInfo>> action) {
        map.forEach(action);
    }

    private Map<String, List<DependencyInfo>> toImmutable(Map<String, List<DependencyInfo>> map) {
        ImmutableMap.Builder<String, List<DependencyInfo>> mapBuilder = ImmutableMap.builder();
        map.forEach((key, values) -> mapBuilder.put(key, ImmutableList.copyOf(values)));
        return mapBuilder.build();
    }
}
