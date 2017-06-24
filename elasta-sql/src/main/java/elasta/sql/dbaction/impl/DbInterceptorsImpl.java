package elasta.sql.dbaction.impl;

import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.sql.dbaction.*;
import elasta.sql.core.SqlQuery;
import elasta.sql.core.UpdateTpl;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by sohan on 4/6/2017.
 */
final public class DbInterceptorsImpl implements DbInterceptors {
    final Collection<UpdateTplInterceptor> updateTplInterceptors;
    final Collection<SqlQueryInterceptor> sqlQueryInterceptors;

    public DbInterceptorsImpl(Collection<UpdateTplInterceptor> updateTplInterceptors, Collection<SqlQueryInterceptor> sqlQueryInterceptors) {
        Objects.requireNonNull(updateTplInterceptors);
        Objects.requireNonNull(sqlQueryInterceptors);
        this.updateTplInterceptors = updateTplInterceptors;
        this.sqlQueryInterceptors = sqlQueryInterceptors;
    }

    @Override
    public Promise<UpdateTpl> interceptUpdate(UpdateTpl updateTpl) {
        return dispatch(updateTplInterceptors, updateTpl);
    }

    @Override
    public Promise<SqlQuery> interceptQuery(SqlQuery sqlQuery) {
        return dispatch(sqlQueryInterceptors, sqlQuery);
    }

    private <T> Promise<T> dispatch(Collection<? extends DeferredInterceptor<T>> deleteDataInterceptors, T data) {
        if (deleteDataInterceptors.size() <= 0) {
            return Promises.of(data);
        }
        Promise<T> promise = Promises.of(data);

        for (DeferredInterceptor<T> deleteDataInterceptor : deleteDataInterceptors) {
            promise = promise.mapP(deleteDataInterceptor::intercept);
        }
        return promise;
    }
}
