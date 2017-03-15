package elasta.orm.delete.dependency.loader;

import elasta.core.promise.intfs.Promise;
import elasta.orm.delete.dependency.TableToTableDataMap;
import elasta.orm.upsert.TableData;

import java.util.List;

/**
 * Created by sohan on 3/12/2017.
 */
public interface MultiTableDependenciesLoader {
    Promise<TableToTableDataMap> load(List<TableData> parentTableDataList);
}
