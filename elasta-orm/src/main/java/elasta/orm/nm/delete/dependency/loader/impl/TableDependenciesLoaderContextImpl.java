package elasta.orm.nm.delete.dependency.loader.impl;

import elasta.orm.nm.upsert.TableData;

import java.util.Objects;
import java.util.Set;

/**
 * Created by sohan on 3/12/2017.
 */
final public class TableDependenciesLoaderContextImpl implements TableDependenciesLoaderContext {
    final Set<TableData> tableDataSet;

    public TableDependenciesLoaderContextImpl(Set<TableData> tableDataSet) {
        Objects.requireNonNull(tableDataSet);
        this.tableDataSet = tableDataSet;
    }

    @Override
    public boolean contains(TableData parentTableData) {
        return tableDataSet.contains(parentTableData);
    }

    @Override
    public void add(TableData parentTableData) {
        tableDataSet.add(parentTableData);
    }
}
