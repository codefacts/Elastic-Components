package elasta.orm.upsert;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-09.
 */
public interface IndirectDependencyHandler {
    Promise<TableData> requireUpsert(TableData parentTableData, JsonObject entity, UpsertContext upsertContext);
}
