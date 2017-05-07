package elasta.orm;

import elasta.core.promise.intfs.Promise;
import elasta.orm.query.QueryExecutor;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by sohan on 4/20/2017.
 */
public interface QueryDataLoader {
    Promise<List<JsonObject>> query(QueryExecutor.QueryParams params);
}
