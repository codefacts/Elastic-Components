package elasta.orm.upsert;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-09.
 */
@FunctionalInterface
public interface UpsertFunction {
    Promise<TableData> upsert(JsonObject jsonObject, UpsertContext upsertContext);
}
