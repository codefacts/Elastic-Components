package elasta.orm.nm.delete.dependency;

import elasta.orm.nm.delete.dependency.impl.DependencyInfo;
import elasta.orm.nm.entitymodel.Entity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by sohan on 3/4/2017.
 */
@FunctionalInterface
public interface DependencyDataLoaderGraphBuilder {
    DependencyDataLoaderGraph build(Map<String, List<DependencyInfo>> tableToTableDependenciesMap);
}
