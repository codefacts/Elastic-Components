package elasta.orm.upsert;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-09.
 */
public interface BelongToHandler {
    Promise<TableData> pushUpsert(JsonObject entity, JsonObject dependencyColumnValues, UpsertContext upsertContext);
}
