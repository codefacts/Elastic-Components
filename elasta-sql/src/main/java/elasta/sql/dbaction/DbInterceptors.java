package elasta.sql.dbaction;

import elasta.core.promise.intfs.Promise;
import elasta.sql.core.SqlQuery;
import elasta.sql.core.UpdateTpl;

/**
 * Created by sohan on 4/5/2017.
 */
public interface DbInterceptors {

    Promise<UpdateTpl> interceptUpdate(UpdateTpl updateTpl);

    Promise<SqlQuery> interceptQuery(SqlQuery sqlQuery);
}
