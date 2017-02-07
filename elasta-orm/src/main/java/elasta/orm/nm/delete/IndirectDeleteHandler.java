package elasta.orm.nm.delete;

import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 17/02/07.
 */
public interface IndirectDeleteHandler {
    void delete(JsonObject parent, JsonObject jsonObject, DeleteContext deleteContext);
}
