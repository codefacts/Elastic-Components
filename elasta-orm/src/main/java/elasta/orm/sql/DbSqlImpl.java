package elasta.orm.sql;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;

import java.util.List;

/**
 * Created by Jango on 9/25/2016.
 */
public class DbSqlImpl implements DbSql {
    @Override
    public Promise<ResultSet> query(String sql) {
        return null;
    }

    @Override
    public Promise<ResultSet> query(String sql, JsonArray params) {
        return null;
    }

    @Override
    public Promise<ResultSet> querySingle(String sql) {
        return null;
    }

    @Override
    public Promise<ResultSet> querySingle(String sql, JsonArray params) {
        return null;
    }

    @Override
    public <T> Promise<T> queryScalar(String sql) {
        return null;
    }

    @Override
    public <T> Promise<T> queryScalar(String sql, JsonArray params) {
        return null;
    }

    @Override
    public Promise<Void> update(String sql) {
        return null;
    }

    @Override
    public Promise<Void> update(String sql, JsonArray params) {
        return null;
    }

    @Override
    public Promise<Void> update(List<String> sqlList) {
        return null;
    }

    @Override
    public Promise<Void> update(List<String> sqlList, List<JsonArray> paramsList) {
        return null;
    }
}
