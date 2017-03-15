package elasta.orm.delete.dependency.loader;

import elasta.core.promise.intfs.Promise;
import elasta.orm.upsert.TableData;

import java.util.List;

/**
 * Created by sohan on 3/4/2017.
 */
public interface DependencyDataLoader {

    String dependentTable();

    Promise<List<TableData>> load(TableData parentTableData);
}
