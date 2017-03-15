package elasta.orm.delete.loader;

import elasta.orm.delete.TableToTableDependenciesMap;

/**
 * Created by sohan on 3/4/2017.
 */
@FunctionalInterface
public interface DependencyDataLoaderGraphBuilder {
    DependencyDataLoaderGraph build(TableToTableDependenciesMap tableToTableDependenciesMap);
}
