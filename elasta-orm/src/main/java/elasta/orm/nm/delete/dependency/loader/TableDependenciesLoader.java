package elasta.orm.nm.delete.dependency.loader;

import elasta.core.promise.intfs.Promise;
import elasta.orm.nm.delete.dependency.TableToTableDataMap;
import elasta.orm.nm.upsert.TableData;

import java.util.List;
import java.util.Map;

/**
 * Created by sohan on 3/8/2017.
 */
public interface TableDependenciesLoader {
    Promise<TableToTableDataMap> load(TableData parentTableData);
}
