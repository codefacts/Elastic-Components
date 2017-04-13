package elasta.orm.event.dbaction;

import elasta.core.promise.intfs.Promise;
import elasta.sql.core.SqlQuery;

/**
 * Created by sohan on 4/14/2017.
 */
public interface SqlQueryInterceptor extends DeferredInterceptor<SqlQuery> {

    Promise<SqlQuery> intercept(SqlQuery sqlQuery);
}
