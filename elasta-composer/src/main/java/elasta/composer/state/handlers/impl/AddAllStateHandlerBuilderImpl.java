package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.InsertAllStateHandlerBuilder;
import elasta.core.flow.Flow;
import elasta.orm.Orm;
import elasta.sql.SqlDB;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 5/21/2017.
 */
final public class AddAllStateHandlerBuilderImpl implements InsertAllStateHandlerBuilder {
    final String entity;
    final SqlDB sqlDB;
    final Orm orm;

    public AddAllStateHandlerBuilderImpl(String entity, SqlDB sqlDB, Orm orm) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(sqlDB);
        Objects.requireNonNull(orm);
        this.entity = entity;
        this.sqlDB = sqlDB;
        this.orm = orm;
    }

    @Override
    public MsgEnterEventHandlerP<JsonArray, JsonArray> build() {
        return msg -> {
            List<JsonObject> list = msg.body().getList();
            return orm.upsertAll(entity, list)
                .map(sqlDB::update)
                .map(jo -> Flow.trigger(Events.next, msg));
        };
    }
}
