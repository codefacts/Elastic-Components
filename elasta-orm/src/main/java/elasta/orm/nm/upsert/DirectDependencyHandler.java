package elasta.orm.nm.upsert;

import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-09.
 */
public interface DirectDependencyHandler {
    TableData requireUpsert(JsonObject entity, UpsertContext upsertContext);
}