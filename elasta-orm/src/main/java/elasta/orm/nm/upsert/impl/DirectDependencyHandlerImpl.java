package elasta.orm.nm.upsert.impl;

import elasta.orm.nm.upsert.*;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-09.
 */
public class DirectDependencyHandlerImpl implements DirectDependencyHandler {
    final UpsertFunction upsertFunction;

    public DirectDependencyHandlerImpl(UpsertFunction upsertFunction) {
        this.upsertFunction = upsertFunction;
    }

    @Override
    public TableData requireUpsert(JsonObject entity, UpsertContext upsertContext) {
        return upsertFunction.upsert(entity, upsertContext);
    }
}
