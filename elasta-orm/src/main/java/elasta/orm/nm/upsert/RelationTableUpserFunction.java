package elasta.orm.nm.upsert;

import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-10.
 */
public interface RelationTableUpserFunction {
    TableData upsert(TableData src, TableData dst, UpsertContext upsertContext);
}
