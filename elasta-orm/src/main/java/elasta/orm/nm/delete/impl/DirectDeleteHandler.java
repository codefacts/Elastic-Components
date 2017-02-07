package elasta.orm.nm.delete.impl;

import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 17/02/07.
 */
public interface DirectDeleteHandler {
    void delete(JsonObject jsonObject);
}
