package tracker.preprocessor.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.Msg;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.query.QueryExecutor;
import elasta.sql.JsonOps;
import io.vertx.core.json.JsonObject;
import tracker.App;
import tracker.TrackerUtils;
import tracker.entity_config.Entities;
import tracker.model.UserModel;
import tracker.DeviceTypes;
import tracker.preprocessor.UserPreprocessor;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Random;

import static elasta.commons.Utils.not;

/**
 * Created by sohan on 7/7/2017.
 */
final public class UserPreprocessorImpl<T> implements UserPreprocessor<T> {
    final QueryExecutor queryExecutor;
    final App.Config config;

    public UserPreprocessorImpl(QueryExecutor queryExecutor, App.Config config) {
        Objects.requireNonNull(queryExecutor);
        Objects.requireNonNull(config);
        this.queryExecutor = queryExecutor;
        this.config = config;
    }

    @Override
    public Promise<JsonObject> apply(Msg<T> msg, JsonObject user) {

        String firstName = user.getString(UserModel.firstName);

        final Promise<String> newUsernamePromise;

        {
            String username = user.getString(UserModel.username);

            if (username == null) {

                newUsernamePromise = generateNewUsername(firstName);
            } else {
                newUsernamePromise = Promises.of(username);
            }
        }

        return Promises.when(
            generateNewUserId(),
            newUsernamePromise
        ).map(tpl2 -> tpl2.apply((newUserId, newUsername) -> {

            LinkedHashMap<String, Object> map = new LinkedHashMap<>(user.getMap());

            map.put(UserModel.userId, newUserId);
            map.put(UserModel.username, newUsername);

            msg.headers().get(TrackerUtils.KEY_ANDROID_DEVICE_TOKEN)
                .ifPresent(token -> {

                    if (not(Objects.equals(token, config.getAndroidDeviceToken()))) {
                        return;
                    }

                    map.put(UserModel.registrationDeviceType, DeviceTypes.android);
                });

            return new JsonObject(ImmutableMap.copyOf(map));

        }));
    }

    private Promise<String> generateNewUsername(String firstName) {

        Objects.requireNonNull(firstName);

        final String username = firstName.toLowerCase();

        return queryExecutor
            .queryArray(
                QueryExecutor.QueryArrayParams.builder()
                    .selections(ImmutableList.of(
                        JsonOps.countDistinct("r." + UserModel.username)
                    ))
                    .entity(Entities.USER)
                    .alias("r")
                    .criteria(JsonOps.like("r." + UserModel.username, username + "-%"))
                    .build()
            )
            .map(jsonArrays -> jsonArrays.get(0).getLong(0))
            .map(count -> username + "-" + (count + 1 + new Random().nextInt(20)))
            ;
    }

    private Promise<String> generateNewUserId() {
        return queryExecutor
            .queryArray(
                QueryExecutor.QueryArrayParams.builder()
                    .selections(ImmutableList.of(
                        JsonOps.countDistinct("r." + UserModel.id)
                    ))
                    .entity(Entities.USER)
                    .alias("r")
                    .build()
            )
            .map(jsonArrays -> jsonArrays.get(0).getLong(0))
            .map(count -> "user-" + (count + 1 + new Random().nextInt(100)));
    }
}
