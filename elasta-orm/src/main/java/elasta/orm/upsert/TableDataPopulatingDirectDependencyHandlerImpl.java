package elasta.orm.upsert;

import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 7/7/2017.
 */
final public class TableDataPopulatingDirectDependencyHandlerImpl implements DirectDependencyHandler {
    final TableDataPopulator tableDataPopulator;

    public TableDataPopulatingDirectDependencyHandlerImpl(TableDataPopulator tableDataPopulator) {
        Objects.requireNonNull(tableDataPopulator);
        this.tableDataPopulator = tableDataPopulator;
    }

    @Override
    public TableData requireUpsert(JsonObject entity, UpsertContext upsertContext) {
        return tableDataPopulator.populate(entity);
    }
}
