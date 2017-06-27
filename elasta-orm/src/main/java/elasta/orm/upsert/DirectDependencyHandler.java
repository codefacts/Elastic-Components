package elasta.orm.upsert;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-09.
 */
public interface DirectDependencyHandler {
    Promise<TableData> requireUpsert(JsonObject entity, UpsertContext upsertContext);
}
