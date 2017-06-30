package elasta.composer.state.handlers.builder.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.builder.InsertStateHandlerBuilder;
import elasta.composer.state.handlers.impl.AddStateHandlerImpl;
import elasta.core.flow.Flow;
import elasta.orm.Orm;
import elasta.sql.SqlDB;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class AddStateHandlerBuilderImpl implements InsertStateHandlerBuilder {
    final String entity;
    final Orm orm;
    final SqlDB sqlDB;

    public AddStateHandlerBuilderImpl(String entity, Orm orm, SqlDB sqlDB) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(orm);
        Objects.requireNonNull(sqlDB);
        this.entity = entity;
        this.orm = orm;
        this.sqlDB = sqlDB;
    }

    @Override
    public MsgEnterEventHandlerP<JsonObject, JsonObject> build() {
        return new AddStateHandlerImpl(entity, orm, sqlDB);
    }
}
