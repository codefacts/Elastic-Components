package elasta.orm.delete.dependency;

import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 3/12/2017.
 */
public interface IndirectChildHandler {
    String field();

    void handle(JsonObject parent, JsonObject child, ListTablesToDeleteContext context);
}
