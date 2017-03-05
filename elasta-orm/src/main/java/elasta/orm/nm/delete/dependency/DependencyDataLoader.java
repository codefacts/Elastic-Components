package elasta.orm.nm.delete.dependency;

import elasta.core.promise.intfs.Promise;
import elasta.orm.nm.upsert.TableData;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by sohan on 3/4/2017.
 */
public interface DependencyDataLoader {

    String dependentTable();

    Promise<List<TableData>> load(TableData parentTableData);
}
