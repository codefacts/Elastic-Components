package elasta.orm.delete.loader.impl;

import elasta.core.promise.intfs.Promise;
import elasta.orm.delete.loader.DependencyDataLoader;import elasta.orm.delete.TableToTableDataMap;
import elasta.orm.delete.loader.DependencyDataLoaderGraph;
import elasta.orm.delete.loader.TableDependenciesLoader;
import elasta.orm.upsert.TableData;

import java.util.*;

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
    public Promise<TableToTableDataMap> load(TableData parentTableData) {

        final Map<TableData, TableData> dataMap = new HashMap<>();

        return new TableDependenciesLoaderByDataMap(
            map,
            dataMap,
            new TableDependenciesLoaderContextImpl(
                new LinkedHashSet<>()
            )
        )
            .load(parentTableData)
            .map(aVoid -> TableDependenciesLoaderByDataMap.toTableToTableDependenciesDataMap(dataMap));
    }

}
