package elasta.orm.nm.upsert;

import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-09.
 */
public interface UpsertContext {

    UpsertContext put(String tableAndPrimaryKey, TableData tableData);

    UpsertContext putOrMerge(String tableAndPrimaryKey, TableData tableData);

    JsonObject get(String tableAndPrimaryKey);

    boolean exists(String tableAndPrimaryKey);

}
