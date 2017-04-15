package elasta.orm.table.data.populator;

import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 4/15/2017.
 */
public interface EntityNameToEntityMap {
    JsonObject get(String entity);
}
