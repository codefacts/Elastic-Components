package elasta.orm.delete.impl;

import elasta.orm.delete.DeleteContext;
import elasta.orm.delete.MultiTableDeleteFunction;
import elasta.orm.delete.TableToTableDataMap;
import elasta.orm.delete.TableToTableDeleteFunctionMap;
import elasta.orm.upsert.TableData;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 3/12/2017.
 */
final public class MultiTableDeleteFunctionImpl implements MultiTableDeleteFunction {
    final TableToTableDeleteFunctionMap tableToTableDeleteFunctionMap;

    public MultiTableDeleteFunctionImpl(TableToTableDeleteFunctionMap tableToTableDeleteFunctionMap) {
        Objects.requireNonNull(tableToTableDeleteFunctionMap);
        this.tableToTableDeleteFunctionMap = tableToTableDeleteFunctionMap;
    }

    @Override
    public void delete(Collection<TableData> tableDataListToDelete, DeleteContext context, TableToTableDataMap tableToTableDataMap) {
        tableDataListToDelete.forEach(
            tableData -> tableToTableDeleteFunctionMap.get(tableData.getTable())
                .delete(tableData, context, tableToTableDataMap)
        );
    }
}
