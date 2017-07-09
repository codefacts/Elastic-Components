package tracker.preprocessor.impl;

import com.google.common.collect.ImmutableMap;
import elasta.composer.Msg;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;
import tracker.App;
import tracker.DeviceTypes;
import tracker.TrackerUtils;
import tracker.model.DeviceModel;
import tracker.preprocessor.DevicePreprocessor;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by sohan on 7/7/2017.
 */
final public class DevicePreprocessorImpl<T> implements DevicePreprocessor<T> {
    final App.Config config;

    public DevicePreprocessorImpl(App.Config config) {
        Objects.requireNonNull(config);
        this.config = config;
    }

    @Override
    public Promise<JsonObject> apply(Msg<T> msg, JsonObject device) {

        String deviceToken = device.getString(TrackerUtils.KEY_ANDROID_DEVICE_TOKEN);

        Objects.requireNonNull(deviceToken);

        final String deviceType = Objects.equals(deviceToken, config.getAndroidDeviceToken()) ? DeviceTypes.android : DeviceTypes.web;

        final String deviceId = generateNewDeviceId(device.getString(DeviceModel.deviceId));

        final LinkedHashMap<String, Object> map = new LinkedHashMap<>(device.getMap());

        map.put(DeviceModel.deviceId, deviceId);
        map.put(DeviceModel.type, deviceType);

        return Promises.of(
            new JsonObject(
                ImmutableMap.copyOf(map)
            )
        );
    }

    private String generateNewDeviceId(String deviceId) {
        return (deviceId != null) ? deviceId : UUID.randomUUID().toString();
    }
}
