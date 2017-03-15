package elasta.orm.delete.dependency.loader.impl;

import elasta.orm.delete.dependency.loader.DependencyDataLoader;import elasta.orm.delete.dependency.loader.DependencyDataLoaderGraph;import elasta.orm.delete.dependency.loader.DependencyDataLoader;
import elasta.orm.delete.dependency.loader.DependencyDataLoaderGraph;
import elasta.orm.delete.dependency.ex.DependencyDataLoaderException;

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

    public Map<String, Collection<DependencyDataLoader>> asMap() {
        return tableToDependencyDataLoadersMap;
    }

    @Override
    public String toString() {
        return "DependencyDataLoaderGraphImpl{" +
            "tableToDependencyDataLoadersMap=" + tableToDependencyDataLoadersMap +
            '}';
    }
}
