package tracker.state.handlers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.model.response.PageModel;
import elasta.composer.state.handlers.FindAllStateHandler;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.intfs.Promise;
import elasta.sql.SqlDB;
import elasta.sql.SqlExecutor;
import elasta.sql.core.JoinData;
import elasta.sql.core.SqlQuery;
import io.vertx.core.json.JsonObject;
import tracker.TrackerUtils;
import tracker.model.PositionModel;
import tracker.model.PositionTable;
import tracker.model.UserModel;
import tracker.model.UserTable;

import java.util.Objects;

/**
 * Created by sohan on 7/9/2017.
 */
final public class FindAllLatestPositionsGroupByUserId implements FindAllStateHandler<JsonObject, JsonObject> {
    final SqlExecutor sqlExecutor;

    public FindAllLatestPositionsGroupByUserId(SqlExecutor sqlExecutor) {
        Objects.requireNonNull(sqlExecutor);
        this.sqlExecutor = sqlExecutor;
    }

    @Override
    public Promise<StateTrigger<Msg<JsonObject>>> handle(Msg<JsonObject> msg) throws Throwable {
        return sqlExecutor
            .query("SELECT r.*, u.* FROM (SELECT * FROM `positions` ORDER BY create_date DESC, created_by ASC) r join `users` u on r.created_by = u.user_id GROUP BY r.created_by")
            .map(resultSet -> {
                if (resultSet.getNumRows() <= 0) {
                    return new JsonObject(ImmutableMap.of(
                        PageModel.content, ImmutableList.of()
                    ));
                }

                ImmutableList.Builder<JsonObject> listBuilder = ImmutableList.builder();

                resultSet.getRows().forEach(jsonObject -> {
                    listBuilder.add(
                        new JsonObject(
                            ImmutableMap.<String, Object>builder()
                                .putAll(
                                    TrackerUtils.copyValues(
                                        jsonObject.getMap(),
                                        ImmutableMap.<String, String>builder()
                                            .putAll(ImmutableMap.of(
                                                PositionTable.id, PositionModel.id,
                                                PositionTable.lat, PositionModel.lat,
                                                PositionTable.lng, PositionModel.lng
                                            ))
                                            .putAll(ImmutableMap.of(
                                                PositionTable.accuracy, PositionModel.accuracy,
                                                PositionTable.time, PositionModel.time,
                                                PositionTable.create_date, PositionModel.createDate
                                            )).build()
                                    )
                                )
                                .putAll(ImmutableMap.of(
                                    PositionModel.createdBy, TrackerUtils.copyValues(
                                        jsonObject.getMap(),
                                        ImmutableMap.<String, String>builder()
                                            .putAll(ImmutableMap.of(
                                                UserTable.user_id, UserModel.userId,
                                                UserTable.username, UserModel.username,
                                                UserTable.first_name, UserModel.firstName
                                            ))
                                            .putAll(ImmutableMap.of(
                                                UserTable.last_name, UserModel.lastName,
                                                UserTable.phone, UserModel.phone,
                                                UserTable.email, UserModel.email
                                            ))
                                            .build()
                                    )
                                ))
                                .build()
                        )
                    );
                });

                return new JsonObject(ImmutableMap.of(
                    PageModel.content, listBuilder.build()
                ));
            })
            .map(r -> Flow.trigger(Events.next, msg.withBody(r)));
    }

    public static void main(String[] asdf) {
        String sql = "SELECT r.*, u.* FROM (SELECT * FROM `positions` ORDER BY create_date DESC, created_by ASC) " +
            "r join `users` u on r.created_by = u.userId GROUP BY created_by";
        System.out.println(sql);
    }
}
