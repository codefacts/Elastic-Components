package elasta.sql;

import elasta.core.promise.intfs.Promise;
import elasta.sql.core.SqlQuery;
import elasta.sql.core.UpdateTpl;
import io.vertx.ext.sql.ResultSet;

import java.util.Collection;

/**
 * Created by sohan on 6/26/2017.
 */
public interface BaseSqlDB {

    Promise<ResultSet> query(SqlQuery sqlQuery);

    Promise<Void> update(Collection<UpdateTpl> sqlList);

}
