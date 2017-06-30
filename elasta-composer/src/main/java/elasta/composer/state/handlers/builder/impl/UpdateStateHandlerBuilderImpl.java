package elasta.composer.state.handlers.builder.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.builder.UpdateStateHandlerBuilder;
import elasta.composer.state.handlers.impl.UpdateStateHandlerImpl;
import elasta.core.flow.Flow;
import elasta.orm.Orm;
import elasta.sql.SqlDB;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class UpdateStateHandlerBuilderImpl implements UpdateStateHandlerBuilder {
    final String entity;
    final Orm orm;
    final SqlDB sqlDB;

    public UpdateStateHandlerBuilderImpl(String entity, Orm orm, SqlDB sqlDB) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(orm);
        Objects.requireNonNull(sqlDB);
        this.entity = entity;
        this.orm = orm;
        this.sqlDB = sqlDB;
    }

    @Override
    public MsgEnterEventHandlerP<JsonObject, JsonObject> build() {
        return new UpdateStateHandlerImpl(
            entity, orm, sqlDB
        );
    }
}
