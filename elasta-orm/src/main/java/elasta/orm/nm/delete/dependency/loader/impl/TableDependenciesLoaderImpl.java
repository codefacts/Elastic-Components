package elasta.orm.nm.delete.dependency.loader.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.nm.delete.dependency.TableToTableDataMap;
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
