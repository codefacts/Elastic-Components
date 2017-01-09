package elasta.orm.nm.upsert;

import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-09.
 */
public interface BelongToHandler {
    TableData pushUpsert(JsonObject entity, JsonObject dependencyColumnValues, UpsertContext upsertContext);
}
