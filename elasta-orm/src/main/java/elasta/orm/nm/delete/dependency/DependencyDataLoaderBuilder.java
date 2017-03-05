package elasta.orm.nm.delete.dependency;

import elasta.orm.nm.delete.dependency.impl.DependencyInfo;
import elasta.orm.nm.entitymodel.Entity;

import java.util.List;
import java.util.Map;

/**
 * Created by sohan on 3/4/2017.
 */
@FunctionalInterface
public interface DependencyDataLoaderBuilder {
    DependencyDataLoader build(DependencyInfo dependencyInfo, List<DependencyInfo> dependentTableDependencies);
}
