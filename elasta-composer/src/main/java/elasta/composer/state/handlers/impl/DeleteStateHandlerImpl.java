package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.interceptor.DbOperationInterceptor;
import elasta.composer.state.handlers.DeleteStateHandler;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.intfs.Promise;
import elasta.orm.Orm;
import elasta.sql.SqlDB;

import java.util.Objects;

/**
 * Created by sohan on 6/30/2017.
 */
final public class DeleteStateHandlerImpl implements DeleteStateHandler<Object, Object> {
    final Orm orm;
    final SqlDB sqlDB;
    final String entity;
    final DbOperationInterceptor dbOperationInterceptor;

    public DeleteStateHandlerImpl(Orm orm, SqlDB sqlDB, String entity, DbOperationInterceptor dbOperationInterceptor) {
        Objects.requireNonNull(orm);
        Objects.requireNonNull(sqlDB);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(dbOperationInterceptor);
        this.orm = orm;
        this.sqlDB = sqlDB;
        this.entity = entity;
        this.dbOperationInterceptor = dbOperationInterceptor;
    }

    @Override
    public Promise<StateTrigger<Msg<Object>>> handle(Msg<Object> msg) throws Throwable {
        return orm.delete(entity, msg.body())
            .map(updateTplList -> updateTplList.stream().map(updateTpl -> dbOperationInterceptor.intercept(updateTpl, msg)))
            .map(sqlDB::update)
            .map(deletedId -> Flow.trigger(Events.next, msg));
    }
}
