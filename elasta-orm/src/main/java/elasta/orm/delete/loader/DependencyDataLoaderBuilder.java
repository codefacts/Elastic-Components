package elasta.orm.delete.loader;

import elasta.orm.delete.loader.impl.DependencyInfo;

import java.util.List;

/**
 * Created by sohan on 3/4/2017.
 */
@FunctionalInterface
public interface DependencyDataLoaderBuilder {
    DependencyDataLoader build(DependencyInfo dependencyInfo, List<DependencyInfo> dependentTableDependencies);
}
