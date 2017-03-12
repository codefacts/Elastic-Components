package elasta.orm.nm.delete.dependency.loader.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.nm.delete.dependency.loader.DependencyDataLoader;
import elasta.orm.nm.delete.dependency.loader.DependencyDataLoaderGraph;
import elasta.orm.nm.delete.dependency.loader.TableDependenciesLoader;
import elasta.orm.nm.upsert.TableData;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/8/2017.
 */
final public class TableDependenciesLoaderImpl implements TableDependenciesLoader {
    final Map<String, Collection<DependencyDataLoader>> map;

    public TableDependenciesLoaderImpl(DependencyDataLoaderGraph graph) {
        Objects.requireNonNull(graph);
        this.map = graph.asMap();
    }

    @Override
    public Promise<Map<String, List<TableData>>> load(TableData parentTableData) {

        final Map<TableData, TableData> dataMap = new HashMap<>();

        return new TableDependenciesLoaderByDataMap(
            map,
            dataMap,
            new TableDependenciesLoaderContextImpl(
                new LinkedHashSet<>()
            )
        )
            .load(parentTableData)
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
