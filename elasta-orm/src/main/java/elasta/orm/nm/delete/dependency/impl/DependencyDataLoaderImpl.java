package elasta.orm.nm.delete.dependency.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.core.promise.intfs.Promise;
import elasta.orm.json.sql.DbSql;
import elasta.orm.nm.delete.dependency.DependencyDataLoader;
import elasta.orm.nm.upsert.ColumnToColumnMapping;
import elasta.orm.nm.upsert.TableData;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/5/2017.
 */
final public class DependencyDataLoaderImpl implements DependencyDataLoader {
    final String dependentTable;
    final ColumnToColumnMapping[] columnMappings;
    final String[] primaryColumns;
    final String[] columns;
    final DbSql dbSql;

    public DependencyDataLoaderImpl(String dependentTable, ColumnToColumnMapping[] columnMappings, String[] primaryColumns, String[] columns, DbSql dbSql) {
        Objects.requireNonNull(dependentTable);
        Objects.requireNonNull(columnMappings);
        Objects.requireNonNull(columns);
        Objects.requireNonNull(dbSql);
        this.dependentTable = dependentTable;
        this.columnMappings = columnMappings;
        this.primaryColumns = primaryColumns;
        this.columns = columns;
        this.dbSql = dbSql;
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
        return dbSql
            .queryWhereJo(
                dependentTable,
                ImmutableList.<String>builder().add(primaryColumns).add(columns).build(),
                new JsonObject(mapBuilder.build())
            )
            .map(
                jsonObjects -> jsonObjects.stream()
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
            ", columns=" + Arrays.toString(columns) +
            ", dbSql=" + dbSql +
            '}';
    }
}
