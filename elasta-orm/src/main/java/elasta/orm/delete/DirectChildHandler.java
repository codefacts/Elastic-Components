package elasta.orm.delete;

import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 3/12/2017.
 */
public interface DirectChildHandler {

    String field();

    void handle(JsonObject jsonObject, ListTablesToDeleteContext context);
}
