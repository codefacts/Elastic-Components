package elasta.orm.sql.sql;

import elasta.core.promise.intfs.Promise;
import elasta.orm.sql.sql.core.SqlCriteria;
import elasta.orm.sql.sql.core.SqlFrom;
import elasta.orm.sql.sql.core.SqlJoin;
import elasta.orm.sql.sql.core.SqlSelection;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;

import java.util.Collection;
import java.util.List;

/**
 * Created by sohan on 3/14/2017.
 */
public interface SqlExecutor {

    Promise<ResultSet> query(String sql);

    Promise<ResultSet> query(String sql, JsonArray params);

    <T> Promise<T> queryScalar(String sql);

    <T> Promise<T> queryScalar(String sql, JsonArray params);

    Promise<Void> update(String sql);

    Promise<Void> update(String sql, JsonArray params);

    Promise<Void> update(List<String> sqlList);

    Promise<Void> update(List<String> sqlList, List<JsonArray> paramsList);

}
