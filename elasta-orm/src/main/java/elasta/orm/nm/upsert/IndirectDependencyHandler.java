package elasta.orm.nm.upsert;

import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-09.
 */
public interface IndirectDependencyHandler {
    TableData requireUpsert(TableData parentTableData, JsonObject entity, UpsertContext upsertContext);
}
