package elasta.orm.delete;

import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 17/02/07.
 */
public interface JoHandler {
    void handle(JsonObject jsonObject);
}
