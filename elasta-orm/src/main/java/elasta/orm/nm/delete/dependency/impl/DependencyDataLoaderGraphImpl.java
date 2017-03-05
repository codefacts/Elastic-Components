package elasta.orm.nm.delete.dependency.impl;

import elasta.orm.nm.delete.dependency.DependencyDataLoader;
import elasta.orm.nm.delete.dependency.DependencyDataLoaderGraph;
import elasta.orm.nm.delete.dependency.ex.DependencyDataLoaderException;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 3/5/2017.
 */
final public class DependencyDataLoaderGraphImpl implements DependencyDataLoaderGraph {
    final Map<String, Collection<DependencyDataLoader>> tableToDependencyDataLoadersMap;

    public DependencyDataLoaderGraphImpl(Map<String, Collection<DependencyDataLoader>> tableToDependencyDataLoadersMap) {
        Objects.requireNonNull(tableToDependencyDataLoadersMap);
        this.tableToDependencyDataLoadersMap = tableToDependencyDataLoadersMap;
    }

    @Override
    public Collection<DependencyDataLoader> get(String table) {
        Collection<DependencyDataLoader> loaders = tableToDependencyDataLoadersMap.get(table);
        if (loaders == null) {
            throw new DependencyDataLoaderException("No DependencyDataLoader exists for dependentTable '" + table + "'");
        }
        return loaders;
    }

    @Override
    public String toString() {
        return "DependencyDataLoaderGraphImpl{" +
            "tableToDependencyDataLoadersMap=" + tableToDependencyDataLoadersMap +
            '}';
    }
}
