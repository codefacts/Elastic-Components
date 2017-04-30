package elasta.orm;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 4/20/2017.
 */
public interface OrmUtils {
    JsonObject EMPTY_JSON_OBJECT = new JsonObject();
    JsonArray EMPTY_JSON_ARRAY = new JsonArray();

    static JsonObject emptyJsonObject() {
        return EMPTY_JSON_OBJECT;
    }

    static JsonArray emptyJsonArray() {
        return EMPTY_JSON_ARRAY;
    }
}
