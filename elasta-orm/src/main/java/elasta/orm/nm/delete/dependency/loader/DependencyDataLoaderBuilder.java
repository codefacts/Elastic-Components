package elasta.orm.nm.delete.dependency.loader;

import elasta.orm.nm.delete.dependency.loader.impl.DependencyInfo;

import java.util.List;

/**
 * Created by sohan on 3/4/2017.
 */
@FunctionalInterface
public interface DependencyDataLoaderBuilder {
    DependencyDataLoader build(DependencyInfo dependencyInfo, List<DependencyInfo> dependentTableDependencies);
}
