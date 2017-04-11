package elasta.orm.delete.loader.impl;

import com.google.common.collect.ImmutableMap;
import elasta.core.promise.intfs.Promise;
import elasta.orm.delete.loader.DependencyDataLoader;
import elasta.sql.core.ColumnToColumnMapping;
import elasta.orm.upsert.TableData;
import elasta.sql.SqlDB;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/5/2017.
 */
final public class DependencyDataLoaderImpl implements DependencyDataLoader {
    final String dependentTable;
    final ColumnToColumnMapping[] columnMappings;
    final String[] primaryColumns;
    final Set<String> columns;
    final SqlDB sqlDB;

    public DependencyDataLoaderImpl(String dependentTable, ColumnToColumnMapping[] columnMappings, String[] primaryColumns, Set<String> columns, SqlDB sqlDB) {
        Objects.requireNonNull(dependentTable);
        Objects.requireNonNull(columnMappings);
        Objects.requireNonNull(columns);
        Objects.requireNonNull(sqlDB);
        this.dependentTable = dependentTable;
        this.columnMappings = columnMappings;
        this.primaryColumns = primaryColumns;
        this.columns = columns;
        this.sqlDB = sqlDB;
    }

    @Override
    public String dependentTable() {
        return dependentTable;
    }

    @Override
    public Promise<List<TableData>> load(TableData parentTableData) {
        ImmutableMap.Builder<String, Object> mapBuilder = ImmutableMap.builder();
        for (ColumnToColumnMapping columnMapping : columnMappings) {
            mapBuilder.put(
                columnMapping.getSrcColumn(),
                parentTableData.getValues().getValue(columnMapping.getDstColumn())
            );
        }
        return sqlDB
            .query(
                dependentTable,
                columns,
                new JsonObject(mapBuilder.build())
            )
            .map(
                resultSet -> resultSet.getRows().stream()
                    .map(jsonObject -> new TableData(dependentTable, primaryColumns, jsonObject))
                    .collect(Collectors.toList())
            );
    }

    @Override
    public String toString() {
        return "DependencyDataLoaderImpl{" +
            "dependentTable='" + dependentTable + '\'' +
            ", columnMappings=" + Arrays.toString(columnMappings) +
            ", primaryColumns=" + Arrays.toString(primaryColumns) +
            ", columns=" + columns +
            ", sqlDB=" + sqlDB +
            '}';
    }
}
