package elasta.orm.delete.dependency.loader;

import elasta.core.promise.intfs.Promise;
import elasta.orm.delete.dependency.TableToTableDataMap;import elasta.orm.delete.dependency.TableToTableDataMap;
import elasta.orm.upsert.TableData;import elasta.orm.upsert.TableData;

/**
 * Created by sohan on 3/8/2017.
 */
public interface TableDependenciesLoader {
    Promise<TableToTableDataMap> load(TableData parentTableData);
}
