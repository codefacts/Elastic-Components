package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.state.handlers.AddAllStateHandler;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.intfs.Promise;
import elasta.orm.Orm;
import elasta.sql.SqlDB;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 6/30/2017.
 */
final public class AddAllStateHandlerImpl implements AddAllStateHandler<JsonArray, JsonArray> {
    final String entity;
    final SqlDB sqlDB;
    final Orm orm;

    public AddAllStateHandlerImpl(String entity, SqlDB sqlDB, Orm orm) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(sqlDB);
        Objects.requireNonNull(orm);
        this.entity = entity;
        this.sqlDB = sqlDB;
        this.orm = orm;
    }

    @Override
    public Promise<StateTrigger<Msg<JsonArray>>> handle(Msg<JsonArray> msg) throws Throwable {
        List<JsonObject> list = msg.body().getList();
        return orm.upsertAll(entity, list)
            .map(sqlDB::update)
            .map(jo -> Flow.trigger(Events.next, msg));
    }
}
