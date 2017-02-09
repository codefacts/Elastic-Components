package elasta.orm.nm.criteria.json.mapping;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Collections;

/**
 * Created by Jango on 2017-01-07.
 */
public interface MappingUtils {
    JsonObject EMPTY_JSON_OBJECT = new JsonObject(Collections.emptyMap());
    JsonArray EMPTY_JSON_ARRAY = new JsonArray(Collections.emptyList());

    static JsonObject emptyJsonObject() {
        return EMPTY_JSON_OBJECT;
    }

    static JsonArray emptyJsonArray() {
        return EMPTY_JSON_ARRAY;
    }
}
