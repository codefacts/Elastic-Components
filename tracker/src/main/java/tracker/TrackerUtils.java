package tracker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.reactivex.functions.Consumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by sohan on 6/25/2017.
 */
public interface TrackerUtils {

    String isNewKey = "$isNew";

    int failureCode = 400;
    String anonymous = "anonymous";
    String KEY_DEVICE_ID = "deviceId";
    String KEY_ANDROID_DEVICE_TOKEN = "androidDeviceToken";
    Consumer NOOPS_DO_ON_NEXT = t -> {
    };
    JsonObject EMPTY_JS = new JsonObject(ImmutableMap.of());
    JsonArray EMPTY_JSON_ARRAY = new JsonArray(ImmutableList.of());

    static Map<String, Object> copyValues(Map<String, Object> srcMap, Map<String, String> keyMapping) {

        ImmutableMap.Builder<String, Object> mapBuilder = ImmutableMap.builder();

        keyMapping.forEach((srcKey, dstKey) -> {

            Object value = srcMap.get(srcKey);

            if (value == null) {
                return;
            }

            mapBuilder.put(dstKey, value);
        });

        return mapBuilder.build();
    }

    static <T> Consumer<T> noopsDoOnNext() {
        return NOOPS_DO_ON_NEXT;
    }

    static JsonObject emptyJsonObject() {
        return EMPTY_JS;
    }

    static JsonArray emptyJsonArray() {
        return EMPTY_JSON_ARRAY;
    }

    static Predicate<? super Map.Entry<String, Object>> filterOutNulls() {
        return entry -> (entry.getKey() != null) && (entry.getValue() != null);
    }
}
