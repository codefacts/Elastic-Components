package elasta.orm.delete.dependency;

import elasta.orm.delete.DeleteContext;import elasta.orm.delete.DeleteContext;
import elasta.orm.upsert.TableData;import elasta.orm.upsert.TableData;

/**
 * Created by sohan on 3/11/2017.
 */
public interface DeleteFunction {
    void delete(TableData tableData, DeleteContext context, TableToTableDataMap tableToTableDataMap);
}
