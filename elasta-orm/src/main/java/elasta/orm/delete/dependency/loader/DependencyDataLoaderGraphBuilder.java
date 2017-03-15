package elasta.orm.delete.dependency.loader;

import elasta.orm.delete.dependency.TableToTableDependenciesMap;import elasta.orm.delete.dependency.TableToTableDependenciesMap;

/**
 * Created by sohan on 3/4/2017.
 */
@FunctionalInterface
public interface DependencyDataLoaderGraphBuilder {
    DependencyDataLoaderGraph build(TableToTableDependenciesMap tableToTableDependenciesMap);
}
