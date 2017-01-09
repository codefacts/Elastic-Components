package elasta.orm.nm.upsert.impl;

import elasta.orm.nm.upsert.BelongToHandler;
import elasta.orm.nm.upsert.TableData;
import elasta.orm.nm.upsert.UpsertContext;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-09.
 */
public class BelongToHandlerImpl implements BelongToHandler {
    @Override
    public TableData pushUpsert(JsonObject entity, JsonObject dependencyColumnValues, UpsertContext upsertContext) {
        return null;
    }
}
