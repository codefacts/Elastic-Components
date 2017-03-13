package elasta.orm.nm.delete.dependency;

import elasta.core.promise.intfs.Promise;
import elasta.orm.nm.upsert.TableData;

import java.util.List;

/**
 * Created by sohan on 3/13/2017.
 */
public interface ReloadTableDataFunction {
    Promise<List<TableData>> reloadIfNecessary(List<TableData> tableDataList);
}
