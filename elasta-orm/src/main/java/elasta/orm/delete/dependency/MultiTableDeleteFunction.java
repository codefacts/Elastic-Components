package elasta.orm.delete.dependency;

import elasta.orm.delete.DeleteContext;import elasta.orm.delete.DeleteContext;
import elasta.orm.upsert.TableData;import elasta.orm.upsert.TableData;

import java.util.List;

/**
 * Created by sohan on 3/12/2017.
 */
public interface MultiTableDeleteFunction {
    void delete(List<TableData> tableDataListToDelete, DeleteContext context, TableToTableDataMap tableToTableDataMap);
}
