package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.interceptor.DbOperationInterceptor;
import elasta.composer.state.handlers.UpdateStateHandler;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.intfs.Promise;
import elasta.orm.Orm;
import elasta.sql.SqlDB;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 6/30/2017.
 */
final public class UpdateStateHandlerImpl implements UpdateStateHandler<JsonObject, JsonObject> {
    final String entity;
    final Orm orm;
    final SqlDB sqlDB;
    final DbOperationInterceptor dbOperationInterceptor;

    public UpdateStateHandlerImpl(String entity, Orm orm, SqlDB sqlDB, DbOperationInterceptor dbOperationInterceptor) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(orm);
        Objects.requireNonNull(sqlDB);
        Objects.requireNonNull(dbOperationInterceptor);
        this.entity = entity;
        this.orm = orm;
        this.sqlDB = sqlDB;
        this.dbOperationInterceptor = dbOperationInterceptor;
    }

    @Override
    public Promise<StateTrigger<Msg<JsonObject>>> handle(Msg<JsonObject> msg) throws Throwable {
        return orm.upsert(entity, msg.body())
            .map(updateTplList -> updateTplList.stream().map(updateTpl -> dbOperationInterceptor.intercept(updateTpl, msg)))
            .mapP(sqlDB::update)
            .map(jo -> Flow.trigger(Events.next, msg));
    }
}
