package elasta.composer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 5/12/2017.
 */
public interface ComposerUtils {
    JsonObject JSON_OBJECT = new JsonObject(ImmutableMap.of());
    JsonArray JSON_ARRAY = new JsonArray(ImmutableList.of());

    static JsonObject emptyJsonObject() {
        return JSON_OBJECT;
    }

    static JsonArray emptyJsonArray() {
        return JSON_ARRAY;
    }

    static MultiMap toMultimap(ListMultimap<String, String> headers) {
        return new VertxMultiMap(headers);
    }
}
