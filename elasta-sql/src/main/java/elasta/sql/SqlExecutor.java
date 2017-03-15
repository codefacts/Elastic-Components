package elasta.sql;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;

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
