package elasta.orm.nm.delete.dependency;

import elasta.orm.nm.delete.dependency.loader.impl.DependencyInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by sohan on 3/11/2017.
 */
public interface DeleteFunctionBuilder {
    DeleteFunction build(String table, DeleteFunctionBuilderContext context, Map<String, List<DependencyInfo>> tableToTableDependenciesMap);
}
