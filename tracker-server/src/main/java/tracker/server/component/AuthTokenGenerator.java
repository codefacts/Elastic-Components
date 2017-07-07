package tracker.server.component;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.Date;
import java.util.Objects;

/**
 * Created by sohan on 7/3/2017.
 */
public interface AuthTokenGenerator {

    Promise<AuthToken> generate(JsonObject user);

    Promise<JsonObject> getData(String token);

    @Value
    @Builder
    final class AuthToken {
        final String authToken;
        final Date expireTime;

        public AuthToken(String authToken, Date expireTime) {
            this.authToken = authToken;
            this.expireTime = expireTime;
            Objects.requireNonNull(authToken);
            Objects.requireNonNull(expireTime);
        }
    }
}
