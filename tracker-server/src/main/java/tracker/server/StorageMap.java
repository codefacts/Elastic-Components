package tracker.server;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Date;
import java.util.Optional;

/**
 * Created by sohan on 7/3/2017.
 */
public interface StorageMap {

    <T> Promise<Void> put(String key, T value, Date expireTime);

    Promise<Optional<JsonObject>> getJsonObject(String key);

    Promise<Optional<JsonArray>> getJsonArray(String key);

    Promise<Optional<Long>> getLong(String key);

    Promise<Optional<Double>> getDouble(String key);

    Promise<Optional<Boolean>> getBoolean(String key);

    Promise<Optional<String>> getString(String key);

    Promise<Optional<Date>> getDate(String key);

    Promise<Boolean> containsKey(String key);

    Promise<Void> remove(String key);

    Promise<Long> count();

}
