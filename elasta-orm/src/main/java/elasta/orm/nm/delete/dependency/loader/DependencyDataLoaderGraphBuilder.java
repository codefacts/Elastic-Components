package elasta.orm.nm.delete.dependency.loader;

import elasta.orm.nm.delete.dependency.TableToTableDependenciesMap;
import elasta.orm.nm.delete.dependency.loader.impl.DependencyInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by sohan on 3/4/2017.
 */
@FunctionalInterface
public interface DependencyDataLoaderGraphBuilder {
    DependencyDataLoaderGraph build(TableToTableDependenciesMap tableToTableDependenciesMap);
}
