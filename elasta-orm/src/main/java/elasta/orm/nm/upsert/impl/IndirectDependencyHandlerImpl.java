package elasta.orm.nm.upsert.impl;

import elasta.orm.nm.upsert.*;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-09.
 */
final public class IndirectDependencyHandlerImpl implements IndirectDependencyHandler {
    final RelationTableUpserFunction relationTableUpserFunction;
    final UpsertFunction childUpsertFunction;

    public IndirectDependencyHandlerImpl(RelationTableUpserFunction relationTableUpserFunction, UpsertFunction childUpsertFunction) {
        this.relationTableUpserFunction = relationTableUpserFunction;
        this.childUpsertFunction = childUpsertFunction;
    }

    @Override
    public TableData requireUpsert(TableData parentTableData, JsonObject entity, UpsertContext upsertContext) {

        TableData childTableData = childUpsertFunction.upsert(entity, upsertContext);

        relationTableUpserFunction.upsert(parentTableData, childTableData, upsertContext);

        return childTableData;
    }
}
