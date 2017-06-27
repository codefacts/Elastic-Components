package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.DeleteAllStateHandlerBuilder;
import elasta.core.flow.Flow;
import elasta.orm.Orm;
import elasta.sql.SqlDB;
import io.vertx.core.json.JsonArray;

import java.util.Objects;

/**
 * Created by sohan on 5/21/2017.
 */
final public class DeleteAllStateHandlerBuilderImpl implements DeleteAllStateHandlerBuilder {
    final Orm orm;
    final SqlDB sqlDB;
    final String entity;

    public DeleteAllStateHandlerBuilderImpl(Orm orm, SqlDB sqlDB, String entity) {
        Objects.requireNonNull(orm);
        Objects.requireNonNull(sqlDB);
        Objects.requireNonNull(entity);
        this.orm = orm;
        this.sqlDB = sqlDB;
        this.entity = entity;
    }

    @Override
    public MsgEnterEventHandlerP<JsonArray, JsonArray> build() {

        return msg -> orm.deleteAll(entity, msg.body().getList())
            .map(sqlDB::update)
            .map(objectList -> Flow.trigger(Events.next, msg));
    }
}
