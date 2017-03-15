package elasta.orm.delete.loader;

import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.delete.TableToTableDataMap;
import elasta.orm.delete.loader.impl.TableDependenciesLoaderByDataMap;
import elasta.orm.delete.loader.impl.TableDependenciesLoaderContextImpl;
import elasta.orm.upsert.TableData;

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
