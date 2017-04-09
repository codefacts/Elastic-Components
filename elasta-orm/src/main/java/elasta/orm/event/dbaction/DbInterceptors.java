package elasta.orm.event.dbaction;

import elasta.core.promise.intfs.Promise;
import elasta.orm.upsert.TableData;
import elasta.sql.core.DeleteData;
import elasta.sql.core.UpdateTpl;

/**
 * Created by sohan on 4/5/2017.
 */
public interface DbInterceptors {

    Promise<DeleteData> interceptDeleteData(DeleteData deleteData);

    Promise<TableData> interceptTableData(TableData tableData);

    Promise<UpdateTpl> interceptUpdateTpl(UpdateTpl updateTpl);
}
