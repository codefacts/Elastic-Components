package elasta.orm.nm.delete.dependency;

import elasta.core.promise.intfs.Promise;
import elasta.orm.nm.upsert.TableData;

import java.util.List;
import java.util.Map;

/**
 * Created by sohan on 3/8/2017.
 */
public interface EntityDependenciesLoader {
    Promise<Map<String, List<TableData>>> load(TableData parentTableData);
}
