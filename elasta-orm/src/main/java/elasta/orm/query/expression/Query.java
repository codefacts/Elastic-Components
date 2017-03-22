package elasta.orm.query.expression;

import elasta.core.promise.intfs.Promise;
import elasta.sql.core.SqlAndParams;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by Jango on 17/02/09.
 */
public interface Query {

    Promise<List<JsonObject>> execute();

    Promise<List<JsonArray>> executeArray();
}
