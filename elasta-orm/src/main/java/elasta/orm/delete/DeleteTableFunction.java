package elasta.orm.delete;

import elasta.orm.upsert.TableData;

/**
 * Created by sohan on 3/11/2017.
 */
public interface DeleteTableFunction {
    void delete(TableData tableData, DeleteContext context, TableToTableDataMap tableToTableDataMap);
}
