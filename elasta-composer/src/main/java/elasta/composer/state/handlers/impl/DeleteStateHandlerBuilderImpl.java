package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.DeleteStateHandlerBuilder;
import elasta.core.flow.Flow;
import elasta.orm.Orm;
import elasta.sql.SqlDB;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class DeleteStateHandlerBuilderImpl implements DeleteStateHandlerBuilder {
    final Orm orm;
    final SqlDB sqlDB;
    final String entity;

    public DeleteStateHandlerBuilderImpl(Orm orm, SqlDB sqlDB, String entity) {
        Objects.requireNonNull(orm);
        Objects.requireNonNull(sqlDB);
        Objects.requireNonNull(entity);
        this.orm = orm;
        this.sqlDB = sqlDB;
        this.entity = entity;
    }

    @Override
    public MsgEnterEventHandlerP<Object, Object> build() {

        return msg -> orm.delete(entity, msg.body())
            .map(sqlDB::update)
            .map(deletedId -> Flow.trigger(Events.next, msg));
    }
}
