package elasta.orm.nm.delete.dependency;

import elasta.orm.nm.delete.dependency.loader.impl.DependencyInfo;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by sohan on 3/13/2017.
 */
public interface TableToTableDependenciesMap {

    List<DependencyInfo> get(String table);

    void forEach(BiConsumer<String, List<DependencyInfo>> action);
}
