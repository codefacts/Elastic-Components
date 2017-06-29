package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.UpdateAllStateHandlerBuilder;
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
final public class UpdateAllStateHandlerBuilderImpl implements UpdateAllStateHandlerBuilder {
    final String entity;
    final Orm orm;
    final SqlDB sqlDB;

    public UpdateAllStateHandlerBuilderImpl(String entity, Orm orm, SqlDB sqlDB) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(orm);
        Objects.requireNonNull(sqlDB);
        this.entity = entity;
        this.orm = orm;
        this.sqlDB = sqlDB;
    }

    @Override
    public MsgEnterEventHandlerP<JsonArray, JsonArray> build() {
        return msg -> {
            List<JsonObject> list = msg.body().getList();
            return orm.upsertAll(entity, list)
                .mapP(sqlDB::update)
                .map(jo -> Flow.trigger(Events.next, msg));
        };
    }
}
