package tracker.preprocessor.impl;

import elasta.composer.Msg;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;
import elasta.composer.AppDateTimeFormatter;
import tracker.TrackerUtils;
import tracker.model.PositionModel;
import tracker.preprocessor.PositionPreprocessor;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * Created by sohan on 7/7/2017.
 */
final public class PositionPreprocessorImpl<T> implements PositionPreprocessor<T> {
    final AppDateTimeFormatter appDateTimeFormatter;

    public PositionPreprocessorImpl(AppDateTimeFormatter appDateTimeFormatter) {
        Objects.requireNonNull(appDateTimeFormatter);
        this.appDateTimeFormatter = appDateTimeFormatter;
    }

    @Override
    public Promise<JsonObject> apply(Msg<T> msg, JsonObject position) {

        LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>(position.getMap());

        msg.headers().get(TrackerUtils.KEY_DEVICE_ID)
            .ifPresent(deviceId -> hashMap.put(PositionModel.deviceId, deviceId));

        hashMap.put(PositionModel.time, time(position.getString(PositionModel.time)));

        return Promises.of(
            new JsonObject(hashMap)
        );
    }

    private String time(String time) {
        return time != null ? time : appDateTimeFormatter.format(Instant.now());
    }
}
