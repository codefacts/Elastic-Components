package tracker.preprocessor.impl;

import elasta.composer.Msg;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;
import tracker.TrackerUtils;
import tracker.model.PositionModel;
import tracker.preprocessor.PositionPreprocessor;

import java.util.LinkedHashMap;

/**
 * Created by sohan on 7/7/2017.
 */
final public class PositionPreprocessorImpl<T> implements PositionPreprocessor<T> {
    @Override
    public Promise<JsonObject> apply(Msg<T> msg, JsonObject position) {

        LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>(position.getMap());

        msg.headers().get(TrackerUtils.KEY_DEVICE_ID)
            .ifPresent(deviceId -> hashMap.put(PositionModel.deviceId, deviceId));

        return Promises.of(
            new JsonObject(hashMap)
        );
    }
}
