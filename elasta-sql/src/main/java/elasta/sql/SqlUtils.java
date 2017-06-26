package elasta.sql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 6/26/2017.
 */
public interface SqlUtils {
    JsonObject JSON_OBJECT = new JsonObject();
    JsonArray JSON_ARRAY = new JsonArray();

    static JsonObject emptyJsonObject() {
        return JSON_OBJECT;
    }

    static JsonArray emptyJsonArray() {
        return JSON_ARRAY;
    }
}
