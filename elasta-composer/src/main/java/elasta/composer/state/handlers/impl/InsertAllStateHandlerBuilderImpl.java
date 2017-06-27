package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.InsertAllStateHandlerBuilder;
import elasta.core.flow.Flow;
import elasta.orm.Orm;
import elasta.sql.SqlDB;
import io.vertx.core.json.JsonArray;

import java.util.Objects;

/**
 * Created by sohan on 5/21/2017.
 */
final public class InsertAllStateHandlerBuilderImpl implements InsertAllStateHandlerBuilder {
    final String entity;
    final SqlDB sqlDB;
    final Orm orm;

    public InsertAllStateHandlerBuilderImpl(String entity, SqlDB sqlDB, Orm orm) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(sqlDB);
        Objects.requireNonNull(orm);
        this.entity = entity;
        this.sqlDB = sqlDB;
        this.orm = orm;
    }

    @Override
    public MsgEnterEventHandlerP<JsonArray, JsonArray> build() {
        return msg -> orm.upsertAll(entity, msg.body().getList())
            .map(sqlDB::update)
            .map(jo -> Flow.trigger(Events.next, msg));
    }
}
