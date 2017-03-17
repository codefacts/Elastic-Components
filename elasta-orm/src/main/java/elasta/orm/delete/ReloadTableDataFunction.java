package elasta.orm.delete;

import elasta.core.promise.intfs.Promise;
import elasta.orm.upsert.TableData;

import java.util.Collection;
import java.util.List;

/**
 * Created by sohan on 3/13/2017.
 */
public interface ReloadTableDataFunction {
    Promise<List<TableData>> reloadIfNecessary(Collection<TableData> tableDataList);
}
