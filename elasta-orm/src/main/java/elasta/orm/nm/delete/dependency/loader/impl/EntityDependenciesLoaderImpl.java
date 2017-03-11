package elasta.orm.nm.delete.dependency.loader.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.nm.delete.dependency.loader.DependencyDataLoader;
import elasta.orm.nm.delete.dependency.loader.DependencyDataLoaderGraph;
import elasta.orm.nm.delete.dependency.loader.EntityDependenciesLoader;
import elasta.orm.nm.upsert.TableData;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/8/2017.
 */
final public class EntityDependenciesLoaderImpl implements EntityDependenciesLoader {
    final Map<String, Collection<DependencyDataLoader>> map;

    public EntityDependenciesLoaderImpl(DependencyDataLoaderGraph graph) {
        Objects.requireNonNull(graph);
        this.map = graph.asMap();
    }

    @Override
    public Promise<Map<String, List<TableData>>> load(TableData parentTableData) {

        if (Utils.not(map.containsKey(parentTableData.getTable()))) {
            return Promises.of(ImmutableMap.of());
        }

        final Map<TableData, TableData> dataMap = new HashMap<>();

        final Collection<DependencyDataLoader> dependencyDataLoaders = map.get(parentTableData.getTable());

        return loadTableDataRecursive(dataMap, dependencyDataLoaders, parentTableData).map(aVoid -> {
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

    private Promise<Void> loadTableDataRecursive(
        Map<TableData, TableData> tableToTableDataSetMap,
        Collection<DependencyDataLoader> dependencyDataLoaders,
        TableData parentTableData
    ) {

        final List<Promise<List<TableData>>> promises = dependencyDataLoaders.stream()
            .map(dependencyDataLoader -> dependencyDataLoader.load(parentTableData))
            .map(
                promise -> promise
                    .thenP(
                        tableDatas -> {

                            putInTable(tableToTableDataSetMap, tableDatas);

                            final List<Promise<Void>> promiseList = tableDatas.stream()
                                .map(
                                    tableData -> loadTableDataRecursive(
                                        tableToTableDataSetMap,
                                        map.get(tableData.getTable()),
                                        tableData
                                    ))
                                .collect(Collectors.toList());

                            return Promises.when(promiseList).map(voids -> (Void) null);

                        }))
            .collect(Collectors.toList());

        return Promises.when(promises).map(lists -> (Void) null);
    }

    private void putInTable(Map<TableData, TableData> dataMap, List<TableData> tableDatas) {

        tableDatas.forEach(tableData -> {

            final String table = tableData.getTable();

            if (Utils.not(
                dataMap.containsKey(tableData)
            )) {
                dataMap.put(tableData, tableData);
                return;
            }

            TableData data = dataMap.get(tableData);
            dataMap.put(
                tableData,
                new TableData(
                    table,
                    tableData.getPrimaryColumns(),
                    new JsonObject(
                        ImmutableMap.<String, Object>builder()
                            .putAll(tableData.getValues())
                            .putAll(data.getValues())
                            .build()
                    )
                )
            );
        });
    }
}
