package elasta.orm.upsert.impl;

import com.google.common.collect.ImmutableMap;
import elasta.orm.upsert.*;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-09.
 */
final public class BelongToHandlerImpl implements BelongToHandler {
    final UpsertFunction upsertFunction;

    public BelongToHandlerImpl(UpsertFunction upsertFunction) {
        Objects.requireNonNull(upsertFunction);
        this.upsertFunction = upsertFunction;
    }

    @Override
    public TableData pushUpsert(JsonObject entity, JsonObject dependencyColumnValues, UpsertContext upsertContext) {

        Objects.requireNonNull(entity);
        Objects.requireNonNull(dependencyColumnValues);
        Objects.requireNonNull(upsertContext);

        TableData tableData = upsertFunction.upsert(entity, upsertContext);

        tableData = new TableData(
            tableData.getTable(),
            tableData.getPrimaryColumns(),
            new JsonObject(
                ImmutableMap.<String, Object>builder()
                    .putAll(tableData.getValues())
                    .putAll(dependencyColumnValues.getMap())
                    .build()
            )
        );

        upsertContext.putOrMerge(
            UpsertUtils.toTableAndPrimaryColumnsKey(
                tableData.getTable(), tableData.getPrimaryColumns(), tableData.getValues()
            ),
            tableData
        );

        return tableData;
    }
}
