package tracker.services.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.AppDateTimeFormatter;
import elasta.orm.BaseOrm;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import elasta.sql.JsonOps;
import elasta.sql.core.Order;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.vertx.core.json.JsonObject;
import tracker.TrackerUtils;
import tracker.entity_config.Entities;
import tracker.model.PositionModel;
import tracker.model.UserModel;
import tracker.model.UserPositionModel;
import tracker.services.ReplayService;

import java.util.*;
import java.util.stream.Collectors;

import static tracker.TrackerUtils.filterOutNulls;

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

        System.out.println("### Loading Data...");

        return loadData(timeStart + count * step)
            .doOnNext(jsonObject -> System.out.println("#### loadData: " + jsonObject.size()))
            .flatMap(timeSlot::buffer)
            .map(userIdToPositionMapGenerator::produce)
            .doOnNext(jsonObject -> System.out.println("### producer returned jsonObject.size() => " + jsonObject.size()))
            .doOnError(throwable -> System.out.println("############ ERROR => " + throwable))
            ;
    }

    private Observable<JsonObject> loadData(long timeEnd) {
        System.out.println("### Observable.create");
        return Observable.create(
            emitter -> orm
                .query(
                    QueryExecutor.QueryParams.builder()
                        .entity(Entities.POSITION_ENTITY)
                        .alias("r")
                        .criteria(
                            JsonOps.and(
                                ImmutableList.of(
                                    JsonOps.gte("r." + PositionModel.time, appDateTimeFormatter.format(new Date(timeStart))),
                                    JsonOps.lt("r." + PositionModel.time, appDateTimeFormatter.format(new Date(timeEnd)))
                                )
                            )
                        )
                        .orderBy(ImmutableList.of(
                            new QueryExecutor.OrderTpl(
                                new FieldExpressionImpl("r." + PositionModel.time),
                                Order.ASC
                            )
                        ))
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
                        emitter.onComplete();
                        return;
                    }

                    for (int i = 0, end = jsonObjects.size() - 1; i < end; i++) {
                        emitter.onNext(jsonObjects.get(i));
                    }

                    final JsonObject lastPosition = jsonObjects.get(jsonObjects.size() - 1);

                    emitter.onNext(new JsonObject(
                        ImmutableMap.<String, Object>builder()
                            .putAll(lastPosition.getMap())
                            .put(KEY_IS_LAST, true)
                            .build()
                    ));
                    emitter.onComplete();

                })
                .err(emitter::onError)
        );
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

            return Observable.defer(() -> {

                final boolean isLast = position.getBoolean(KEY_IS_LAST, false);
                final long time = time(position);

                System.out.println("### isLast: " + isLast);

                if (time < endTime) {
                    builder.add(position);
                    System.out.println("### returning Observable.empty");
                    return isLast ? Observable.just(builder.build()) : Observable.empty();
                }

                final List<JsonObject> list = builder.build();

                final long emptySlots = (time - endTime) / step;

                builder = ImmutableList.builder();
                endTime = endTime + this.step * (emptySlots + 1);

                builder.add(position);

                if (isLast) {

                    List<List<JsonObject>> lists = ImmutableList.<List<JsonObject>>builder()
                        .add(list)
                        .addAll(emptySlotsList(emptySlots))
                        .add(builder.build())
                        .build();

                    System.out.println("### returning buffer ## last elements: " + lists.size());
                    return Observable.fromIterable(
                        lists
                    );
                }

                final List<List<JsonObject>> lists = ImmutableList.<List<JsonObject>>builder()
                    .add(list)
                    .addAll(emptySlotsList(emptySlots))
                    .build();

                System.out.println("### returning buffer elements: " + lists.size());
                return Observable.fromIterable(lists);
            });
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

        public JsonObject produce(List<JsonObject> jsonObjects) {

            System.out.println("### producer input: " + jsonObjects.size());

            final HashMap<String, Object> map = new HashMap<>();

            jsonObjects.forEach(position -> {
                final String userId = position.getJsonObject(PositionModel.createdBy).getString(UserModel.userId);
                map.put(userId, toUserPositionModel(position));
            });

            return new JsonObject(ImmutableMap.copyOf(map));
        }

        private JsonObject toUserPositionModel(JsonObject position) {

            final ImmutableMap.Builder<String, Object> mapBuilder = ImmutableMap.builder();

            final JsonObject user = position.getJsonObject(PositionModel.createdBy);

            mapBuilder.putAll(
                user.getMap().entrySet().stream()
                    .filter(filterOutNulls())
                    .collect(Collectors.toList())
            );

            mapBuilder.put(UserPositionModel.position, new JsonObject(
                ImmutableMap.of(
                    PositionModel.id, position.getValue(PositionModel.id),
                    PositionModel.lat, position.getValue(PositionModel.lat),
                    PositionModel.lng, position.getValue(PositionModel.lng),
                    PositionModel.time, position.getValue(PositionModel.time)
                )
            ));

            return new JsonObject(mapBuilder.build());
        }
    }
}
