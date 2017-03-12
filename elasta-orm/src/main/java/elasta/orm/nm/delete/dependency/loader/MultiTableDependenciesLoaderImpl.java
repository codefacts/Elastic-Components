package elasta.orm.nm.delete.dependency.loader;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.nm.delete.dependency.TableToTableDataMap;
import elasta.orm.nm.delete.dependency.loader.impl.TableDependenciesLoaderByDataMap;
import elasta.orm.nm.delete.dependency.loader.impl.TableDependenciesLoaderContextImpl;
import elasta.orm.nm.upsert.TableData;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/12/2017.
 */
final public class MultiTableDependenciesLoaderImpl implements MultiTableDependenciesLoader {
    final Map<String, Collection<DependencyDataLoader>> map;

    public MultiTableDependenciesLoaderImpl(DependencyDataLoaderGraph graph) {
        Objects.requireNonNull(graph);
        this.map = graph.asMap();
    }

    @Override
    public Promise<TableToTableDataMap> load(List<TableData> parentTableDataList) {

        final Map<TableData, TableData> dataMap = new HashMap<>();

        TableDependenciesLoaderByDataMap tableDependenciesLoaderByDataMap = new TableDependenciesLoaderByDataMap(map, dataMap, new TableDependenciesLoaderContextImpl(new LinkedHashSet<>()));

        List<Promise<Void>> promiseList = parentTableDataList.stream()
            .map(tableDependenciesLoaderByDataMap::load)
            .collect(Collectors.toList());

        return Promises.when(promiseList)
            .map(aVoid -> TableDependenciesLoaderByDataMap.toTableToTableDependenciesDataMap(dataMap));
    }
}
