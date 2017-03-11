package elasta.orm.nm.delete.dependency.loader;

import elasta.orm.nm.delete.dependency.loader.impl.DependencyInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by sohan on 3/11/2017.
 */
public interface TableToTableDependenciesMapBuilder {
    Map<String, List<DependencyInfo>> build();
}
