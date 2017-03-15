package elasta.orm.delete;

import elasta.orm.upsert.TableData;

import java.util.List;

/**
 * Created by sohan on 3/12/2017.
 */
public interface MultiTableDeleteFunction {
    void delete(List<TableData> tableDataListToDelete, DeleteContext context, TableToTableDataMap tableToTableDataMap);
}
