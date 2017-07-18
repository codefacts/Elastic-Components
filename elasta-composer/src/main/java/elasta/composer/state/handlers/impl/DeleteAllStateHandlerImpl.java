package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.interceptor.DbOperationInterceptor;
import elasta.composer.state.handlers.DeleteAllStateHandler;
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
final public class DeleteAllStateHandlerImpl implements DeleteAllStateHandler<JsonArray, JsonArray> {
    final Orm orm;
    final SqlDB sqlDB;
    final String entity;
    final DbOperationInterceptor dbOperationInterceptor;

    public DeleteAllStateHandlerImpl(Orm orm, SqlDB sqlDB, String entity, DbOperationInterceptor dbOperationInterceptor) {
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
    public Promise<StateTrigger<Msg<JsonArray>>> handle(Msg<JsonArray> msg) throws Throwable {
        List<JsonObject> list = msg.body().getList();
        return orm.deleteAll(entity, list)
            .map(updateTplList -> updateTplList.stream().map(updateTpl -> dbOperationInterceptor.intercept(updateTpl, msg)))
            .mapP(sqlDB::update)
            .map(objectList -> Flow.trigger(Events.next, msg));
    }
}
