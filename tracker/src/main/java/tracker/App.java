package tracker;

import elasta.composer.MessageBus;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 6/25/2017.
 */
public interface App {

    App close();

    MessageBus mesageBus();

    @Value
    @Builder
    final class Config {
        final JsonObject db;
        final Map<String, String> messageBundle;
        final Vertx vertx;
        final long defaultPage;
        final int defaultPageSize;
        final String rootAlias;
        final String androidDeviceToken;
        final long authTokenExpireTime;

        public Config(JsonObject db, Map<String, String> messageBundle, Vertx vertx, long defaultPage, int defaultPageSize, String rootAlias, String androidDeviceToken, long authTokenExpireTime) {
            Objects.requireNonNull(db);
            Objects.requireNonNull(messageBundle);
            Objects.requireNonNull(vertx);
            Objects.requireNonNull(rootAlias);
            Objects.requireNonNull(androidDeviceToken);
            this.db = db;
            this.messageBundle = messageBundle;
            this.vertx = vertx;
            this.androidDeviceToken = androidDeviceToken;
            //ValueTypes
            this.defaultPage = defaultPage;
            this.defaultPageSize = defaultPageSize;
            this.rootAlias = rootAlias;
            this.authTokenExpireTime = authTokenExpireTime;
        }

        public Object getAndroidDeviceToken() {
            return androidDeviceToken;
        }

        public long getAuthTokenExpireTime() {
            return authTokenExpireTime;
        }
    }

    static void main(String[] asdf) {
        String prettily = new JsonObject(
            "{\"id\":\"1\",\"user_id\":\"admin-1\",\"username\":\"admin\",\"email\":\"admin@admin\",\"phone\":\"01951883412\",\"created_by\":\"1\",\"updated_by\":\"1\",\"create_date\":\"2017-06-07 00:00:00\",\"update_date\":\"2017-06-22 00:00:00\"}"
        ).encodePrettily();
        System.out.println(prettily);
    }
}
