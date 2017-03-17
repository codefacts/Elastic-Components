package elasta.orm.delete.impl;

import elasta.orm.delete.TableToTableDataMap;
import elasta.orm.delete.ex.TableDataMapException;
import elasta.orm.upsert.TableData;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 3/12/2017.
 */
final public class TableToTableDataMapImpl implements TableToTableDataMap {
    final Map<String, Map<TableData, TableData>> tableToTableDataMap;

    public TableToTableDataMapImpl(Map<String, Map<TableData, TableData>> tableToTableDataMap) {
        Objects.requireNonNull(tableToTableDataMap);
        this.tableToTableDataMap = tableToTableDataMap;
    }

    @Override
    public Collection<TableData> getAsCollection(String dependentTable) {
        return getTableDataMap(dependentTable).values();
    }

    private Map<TableData, TableData> getTableDataMap(String dependentTable) {
        Map<TableData, TableData> tableDataMap = tableToTableDataMap.get(dependentTable);
        if (tableDataMap == null) {
            throw new TableDataMapException("No TableData exists for table '" + dependentTable + "'");
        }
        return tableDataMap;
    }
}
