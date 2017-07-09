package tracker.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by sohan on 7/3/2017.
 */
public interface ServerUtils {
    String BEARER = "Bearer ";
    String CURRENT_USER = "currentUser";
    String APPLICATION_JSON = "application/json";
    String ANONYMOUS = "anonymous";
    String APPLICATION_JSON_UTF_8 = "application/json; charset=utf-8";
    String TEXT_PLAIN_UTF_8 = "text/plain; charset=utf-8";
    JsonObject EMPTY_JSON_OBJECT = new JsonObject(ImmutableMap.of());
    JsonArray EMPTY_JSON_ARRAY = new JsonArray(ImmutableList.of());
    String TEXT_PLAIN = "text/plain";
    String CUSTOM_HEADER_PREFIX = "XX-";

    static JsonObject emptyJsonObject() {
        return EMPTY_JSON_OBJECT;
    }

    static JsonArray emptyJsonArray() {
        return EMPTY_JSON_ARRAY;
    }

    static long timeDiff(Date date1, Date date2, TimeUnit minutes) {
        return minutes.convert(date1.getTime() - date2.getTime(), TimeUnit.MILLISECONDS);
    }
}
