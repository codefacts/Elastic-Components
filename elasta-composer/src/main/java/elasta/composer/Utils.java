package elasta.composer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 11/13/2016.
 */
public interface Utils {

    JsonObject EMPTY_JSON_OBJECT = new JsonObject(ImmutableMap.of());
    JsonArray EMPTY_JSON_ARRAY = new JsonArray(ImmutableList.of());

    static JsonObject emptyJsonObject() {
        return EMPTY_JSON_OBJECT;
    }

    static JsonArray emptyJsonArray() {
        return EMPTY_JSON_ARRAY;
    }
}
