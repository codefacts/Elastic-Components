package tracker;

import com.google.common.collect.ImmutableMap;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 6/25/2017.
 */
public interface AppUtils {

    static JsonObject toJsonConfig(App.DB db) {

        return new JsonObject(
            ImmutableMap.<String, Object>builder()
                .putAll(
                    ImmutableMap.of(
                        "user", db.getUser(),
                        "password", db.getPassword(),
                        "host", db.getHost()
                    )
                )
                .putAll(
                    ImmutableMap.of(
                        "port", db.getPort(),
                        "database", db.getDatabase(),
                        "driverClassName", db.getDriverClassName()
                    )
                )
                .build()
        );
    }
}
