package elasta.orm.upsert.impl;

import com.google.common.collect.ImmutableMap;
import elasta.core.promise.intfs.Promise;
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
    public Promise<TableData> pushUpsert(JsonObject entity, JsonObject dependencyColumnValues, UpsertContext upsertContext) {

        Objects.requireNonNull(entity);
        Objects.requireNonNull(dependencyColumnValues);
        Objects.requireNonNull(upsertContext);

        return upsertFunction.upsert(entity, upsertContext)
            .map(tableData -> {
                tableData = tableData.addValues(
                    dependencyColumnValues.getMap()
                );

                upsertContext.putOrMerge(
                    UpsertUtils.toTableAndPrimaryColumnsKey(
                        tableData.getTable(), tableData.getPrimaryColumns(), tableData.getValues()
                    ),
                    tableData
                );

                return tableData;
            })
            ;
    }
}
