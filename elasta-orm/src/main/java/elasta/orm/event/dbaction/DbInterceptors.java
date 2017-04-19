package elasta.orm.event.dbaction;

import elasta.core.promise.intfs.Promise;
import elasta.sql.core.SqlQuery;
import elasta.sql.core.UpdateTpl;

/**
 * Created by sohan on 4/5/2017.
 */
public interface DbInterceptors {

    Promise<UpdateTpl> interceptUpdateTpl(UpdateTpl updateTpl);

    Promise<SqlQuery> interceptSqlQuery(SqlQuery sqlQuery);
}
