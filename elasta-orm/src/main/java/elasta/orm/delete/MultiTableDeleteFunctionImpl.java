package elasta.orm.delete;

import elasta.orm.upsert.TableData;

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
    public void delete(List<TableData> tableDataListToDelete, DeleteContext context, TableToTableDataMap tableToTableDataMap) {
        tableDataListToDelete.forEach(
            tableData -> tableToTableDeleteFunctionMap.get(tableData.getTable())
                .delete(tableData, context, tableToTableDataMap)
        );
    }
}
