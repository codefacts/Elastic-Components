package tracker.preprocessor.merchandiser.impl;

import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.composer.Msg;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;
import tracker.ex.OutletPreprocessorException;
import tracker.model.merchandiser.OutletModel;
import tracker.preprocessor.merchandiser.OutletPreprocessor;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 7/18/2017.
 */
final public class OutletPreprocessorImpl<T> implements OutletPreprocessor<T> {
    @Override
    public Promise<JsonObject> apply(Msg<T> msg, JsonObject jsonObject) {

        JsonObject locationGps = jsonObject.getJsonObject(OutletModel.locationGps);
        JsonObject locationNetwork = jsonObject.getJsonObject(OutletModel.locationNetwork);

        JsonObject location = (locationGps != null) ? locationGps : locationNetwork;

        if (location == null) {
            throw new OutletPreprocessorException("LocationGps and LocationNetwork both can not be null at the same time");
        }

        return Promises.of(
            new JsonObject(
                ImmutableMap.<String, Object>builder()
                    .putAll(
                        jsonObject.getMap().entrySet().stream()
                            .filter(entry -> Utils.not(Objects.equals(entry.getKey(), OutletModel.location)))
                            .collect(Collectors.toList())
                    )
                    .put(OutletModel.location, location)
                    .build()
            )
        );
    }
}
