package elasta.orm.upsert.impl;

import elasta.core.promise.intfs.Promise;
import elasta.orm.upsert.*;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-09.
 */
final public class IndirectDependencyHandlerImpl implements IndirectDependencyHandler {
    final RelationTableUpserFunction relationTableUpserFunction;
    final UpsertFunction childUpsertFunction;

    public IndirectDependencyHandlerImpl(RelationTableUpserFunction relationTableUpserFunction, UpsertFunction childUpsertFunction) {
        Objects.requireNonNull(relationTableUpserFunction);
        Objects.requireNonNull(childUpsertFunction);
        this.relationTableUpserFunction = relationTableUpserFunction;
        this.childUpsertFunction = childUpsertFunction;
    }

    @Override
    public Promise<TableData> requireUpsert(TableData parentTableData, JsonObject entity, UpsertContext upsertContext) {

        Objects.requireNonNull(parentTableData);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(upsertContext);

        return childUpsertFunction.upsert(entity, upsertContext)
            .map(childTableData -> {
                relationTableUpserFunction.upsert(parentTableData, childTableData, upsertContext);
                return childTableData;
            });
    }
}
