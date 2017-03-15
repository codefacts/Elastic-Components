package elasta.orm.delete;

import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 17/02/07.
 */
public interface RelationTableDeleteFunction {
    void delete(JsonObject parent, JsonObject jsonObject, DeleteContext deleteContext);
}
