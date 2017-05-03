package elasta.orm.delete;

import elasta.orm.upsert.TableData;

import java.util.Collection;
import java.util.List;

/**
 * Created by sohan on 3/12/2017.
 */
public interface MultiTableDeleteFunction {
    void delete(Collection<TableData> tableDataListToDelete, DeleteContext context, TableToTableDataMap tableToTableDataMap);
}
