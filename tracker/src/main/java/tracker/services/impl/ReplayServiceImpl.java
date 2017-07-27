package tracker.services.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.AppDateTimeFormatter;
import elasta.orm.BaseOrm;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import elasta.sql.JsonOps;
import io.reactivex.Observable;
import io.vertx.core.json.JsonObject;
import tracker.entity_config.Entities;
import tracker.model.PositionModel;
import tracker.model.UserModel;
import tracker.model.UserPositionModel;
import tracker.services.ReplayService;

import java.util.*;

/**
 * Created by sohan on 2017-07-26.
 */
final public class ReplayServiceImpl implements ReplayService {
    private static final String KEY_IS_LAST = "$$$isLast";
    final BaseOrm orm;
    final AppDateTimeFormatter appDateTimeFormatter;
    final long timeStart;
    final long step;

    public ReplayServiceImpl(BaseOrm orm, AppDateTimeFormatter appDateTimeFormatter, long timeStart, long step) {
        this.timeStart = timeStart;
        this.step = step;
        Objects.requireNonNull(appDateTimeFormatter);
        Objects.requireNonNull(orm);
        this.appDateTimeFormatter = appDateTimeFormatter;
        this.orm = orm;
    }

    @Override
    public Observable<JsonObject> next(int count) {

        final TimeSlot timeSlot = new TimeSlot(timeStart, step);
        UserIdToPositionMapGenerator userIdToPositionMapGenerator = new UserIdToPositionMapGenerator();

        Observable<JsonObject> dataObservable = loadData(timeStart + count * step);

        return dataObservable
            .flatMap(timeSlot::buffer)
            .map(userIdToPositionMapGenerator::produce)
            ;
    }

    private Observable<JsonObject> loadData(long timeEnd) {
        return Observable.fromPublisher(subscriber -> {
            orm
                .query(
                    QueryExecutor.QueryParams.builder()
                        .entity(Entities.POSITION_ENTITY)
                        .alias("r")
                        .criteria(
                            JsonOps.and(
                                ImmutableList.of(
                                    JsonOps.gte("r.time", appDateTimeFormatter.format(new Date(timeStart))),
                                    JsonOps.lt("r.time", appDateTimeFormatter.format(new Date(timeEnd)))
                                )
                            )
                        )
                        .selections(
                            ImmutableList.of(
                                new FieldExpressionImpl("r." + PositionModel.id),
                                new FieldExpressionImpl("r." + PositionModel.lat),
                                new FieldExpressionImpl("r." + PositionModel.lng),
                                new FieldExpressionImpl("r." + PositionModel.time),
                                new FieldExpressionImpl("r." + PositionModel.createdBy + "." + UserModel.id),
                                new FieldExpressionImpl("r." + PositionModel.createdBy + "." + UserModel.userId),
                                new FieldExpressionImpl("r." + PositionModel.createdBy + "." + UserModel.username),
                                new FieldExpressionImpl("r." + PositionModel.createdBy + "." + UserModel.firstName),
                                new FieldExpressionImpl("r." + PositionModel.createdBy + "." + UserModel.lastName)
                            )
                        )
                        .build()
                )
                .then(jsonObjects -> {

                    if (jsonObjects.isEmpty()) {
                        subscriber.onComplete();
                        return;
                    }

                    for (int i = 0, end = jsonObjects.size() - 1; i < end; i++) {
                        subscriber.onNext(jsonObjects.get(i));
                    }

                    final JsonObject lastPosition = jsonObjects.get(jsonObjects.size() - 1);

                    subscriber.onNext(new JsonObject(
                        ImmutableMap.<String, Object>builder()
                            .putAll(lastPosition.getMap())
                            .put(KEY_IS_LAST, true)
                            .build()
                    ));
                    subscriber.onComplete();

                })
                .err(subscriber::onError)
            ;
        });
    }

    private long time(JsonObject jsonObject) {
        return appDateTimeFormatter.parseDate(jsonObject.getString(PositionModel.time)).getTime();
    }

    private final class TimeSlot {
        final long step;
        long endTime;
        ImmutableList.Builder<JsonObject> builder;

        public TimeSlot(long startTime, long step) {
            this.step = step;
            endTime = startTime + step;
            builder = ImmutableList.builder();
        }

        public Observable<List<JsonObject>> buffer(JsonObject position) {

            final boolean isLast = position.getBoolean(KEY_IS_LAST, false);
            final long time = time(position);

            if (time < endTime) {
                builder.add(position);
                return isLast ? Observable.just(builder.build()) : Observable.empty();
            }

            final List<JsonObject> list = builder.build();

            final long emptySlots = (time - endTime) / step;

            builder = ImmutableList.builder();
            endTime = endTime + this.step * (emptySlots + 1);

            builder.add(position);

            if (isLast) {
                return Observable.fromIterable(
                    ImmutableList.<List<JsonObject>>builder()
                        .add(list)
                        .addAll(emptySlotsList(emptySlots))
                        .add(builder.build())
                        .build()
                );
            }

            return Observable.fromIterable(
                ImmutableList.<List<JsonObject>>builder()
                    .add(list)
                    .addAll(emptySlotsList(emptySlots))
                    .build()
            );
        }

        private List<List<JsonObject>> emptySlotsList(long emptySlots) {
            ImmutableList.Builder<List<JsonObject>> builder = ImmutableList.builder();
            for (int i = 0; i < emptySlots; i++) {
                builder.add(ImmutableList.of());
            }
            return builder.build();
        }
    }

    private final static class UserIdToPositionMapGenerator {
        private JsonObject lastUserIdToPositionMap;

        public JsonObject produce(List<JsonObject> jsonObjects) {

            final HashMap<String, Object> map = new HashMap<>();

            jsonObjects.forEach(position -> {
                final String userId = position.getJsonObject(PositionModel.createdBy).getString(UserModel.userId);
                map.put(userId, toUserPositionModel(position));
            });

            if (lastUserIdToPositionMap == null) {
                return new JsonObject(map);
            }

            return lastUserIdToPositionMap = differentiate(lastUserIdToPositionMap, new JsonObject(map));
        }

        private JsonObject differentiate(JsonObject prev, JsonObject curr) {

            HashSet<String> curUserIds = new HashSet<>(curr.fieldNames());

            prev.fieldNames().forEach(userId -> {

                curUserIds.remove(userId);

                final JsonObject user = curr.getJsonObject(userId);

                if (user == null) {

                    final JsonObject prevUser =
                        prev
                            .getJsonObject(userId)
                            .put(UserPositionModel.positionStatus, UserPositionModel.PositionStatus.$absent);

                    curr.put(
                        userId, prevUser
                    );
                    return;
                }

                user.put(
                    UserPositionModel.positionStatus,
                    UserPositionModel.PositionStatus.$present
                );
            });

            curUserIds.forEach(userId -> curr.getJsonObject(userId).put(UserPositionModel.positionStatus, UserPositionModel.PositionStatus.$new));

            return curr;
        }

        private JsonObject toUserPositionModel(JsonObject position) {

            HashMap<String, Object> map = new HashMap<>();

            final JsonObject user = position.getJsonObject(PositionModel.createdBy);

            map.putAll(user.getMap());
            map.put(UserPositionModel.positionStatus, UserPositionModel.PositionStatus.$new);

            map.put(UserPositionModel.position, new JsonObject(
                ImmutableMap.of(
                    PositionModel.id, position.getDouble(PositionModel.id),
                    PositionModel.lat, position.getDouble(PositionModel.lat),
                    PositionModel.lng, position.getDouble(PositionModel.lng),
                    PositionModel.time, position.getDouble(PositionModel.time)
                )
            ));

            return new JsonObject(map);
        }
    }
}
