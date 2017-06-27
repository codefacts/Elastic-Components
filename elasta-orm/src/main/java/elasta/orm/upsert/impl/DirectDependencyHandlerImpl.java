package elasta.orm.upsert.impl;

import elasta.orm.upsert.DirectDependencyHandler;import elasta.orm.upsert.TableData;import elasta.orm.upsert.UpsertContext;import elasta.orm.upsert.UpsertFunction;import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-09.
 */
final public class DirectDependencyHandlerImpl implements DirectDependencyHandler {
    final UpsertFunction upsertFunction;

    public DirectDependencyHandlerImpl(UpsertFunction upsertFunction) {
        Objects.requireNonNull(upsertFunction);
        this.upsertFunction = upsertFunction;
    }

    @Override
    public TableData requireUpsert(JsonObject entity, UpsertContext upsertContext) {
        return upsertFunction.upsert(entity, upsertContext);
    }
}
