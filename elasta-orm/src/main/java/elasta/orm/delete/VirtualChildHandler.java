package elasta.orm.delete;

import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 3/12/2017.
 */
public interface VirtualChildHandler {
    String field();

    void handle(JsonObject parentEntity, JsonObject childEntity, ListTablesToDeleteContext context);
}
