package elasta.orm.nm.delete.dependency.loader;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
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
    public Promise<Map<String, List<TableData>>> load(List<TableData> parentTableDataList) {

        final Map<TableData, TableData> dataMap = new HashMap<>();

        TableDependenciesLoaderByDataMap tableDependenciesLoaderByDataMap = new TableDependenciesLoaderByDataMap(map, dataMap, new TableDependenciesLoaderContextImpl(new LinkedHashSet<>()));

        List<Promise<Void>> promiseList = parentTableDataList.stream()
            .map(tableDependenciesLoaderByDataMap::load)
            .collect(Collectors.toList());

        return Promises.when(promiseList)
            .map(aVoid -> {
                final Map<String, List<TableData>> map = new HashMap<>();

                dataMap.forEach((td, tableData) -> {
                    final String table = tableData.getTable();
                    List<TableData> tableDataList = map.get(table);
                    if (tableDataList == null) {
                        map.put(table, tableDataList = new ArrayList<>());
                    }
                    tableDataList.add(
                        tableData
                    );
                });

                return copyRecursive(map);
            });
    }

    private Map<String, List<TableData>> copyRecursive(Map<String, List<TableData>> map) {
        ImmutableMap.Builder<String, List<TableData>> mapBuilder = ImmutableMap.builder();
        map.forEach((table, tableDatas) -> {
            mapBuilder.put(table, ImmutableList.copyOf(tableDatas));
        });
        return mapBuilder.build();
    }
}
