package tracker.server.component.impl;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;
import tracker.server.StorageMap;
import tracker.server.component.AuthTokenGenerator;
import tracker.server.component.ex.AuthTokenGeneratorException;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by sohan on 7/3/2017.
 */
final public class AuthTokenGeneratorImpl implements AuthTokenGenerator {
    final StorageMap storageMap;
    final long expireDuration;
    final TimeUnit timeUnit;

    public AuthTokenGeneratorImpl(StorageMap storageMap, long expireDuration, TimeUnit timeUnit) {
        Objects.requireNonNull(storageMap);
        Objects.requireNonNull(expireDuration);
        Objects.requireNonNull(timeUnit);
        this.storageMap = storageMap;
        this.expireDuration = expireDuration;
        this.timeUnit = timeUnit;
    }

    @Override
    public Promise<AuthToken> generate(JsonObject user) {

        String token = UUID.randomUUID().toString();

        return storageMap.put(token, user, expireTime())
            .map(aVoid -> new AuthToken(
                token,
                expireTime()
            ));
    }

    @Override
    public Promise<JsonObject> getData(String token) {
        return storageMap.getJsonObject(token).map(entries -> entries.orElseThrow(() -> new AuthTokenGeneratorException("Token '" + token + "' is not valid")));
    }

    private Date expireTime() {
        return new Date(
            System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(expireDuration, timeUnit)
        );
    }
}
