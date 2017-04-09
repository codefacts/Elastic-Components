package elasta.orm.upsert;

import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-09.
 */
@FunctionalInterface
public interface DependencyColumnValuePopulator {
    JsonObject populate(JsonObject childTableData);
}
