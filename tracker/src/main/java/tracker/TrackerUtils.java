package tracker;

import com.google.common.collect.ImmutableMap;
import elasta.orm.entity.EntityMappingHelper;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by sohan on 6/25/2017.
 */
public interface TrackerUtils {

    String isNewKey = "$isNew";

    int failureCode = 400;
    String anonymous = "anonymous";
    String KEY_DEVICE_ID = "deviceId";
    String KEY_ANDROID_DEVICE_TOKEN = "androidDeviceToken";

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
}
