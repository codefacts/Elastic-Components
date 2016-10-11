package elasta.orm.json.sql;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;

import java.util.List;

/**
 * Created by Jango on 9/25/2016.
 */
public class DbSqlImpl implements DbSql {
    private final JDBCClient jdbcClient;

    public DbSqlImpl(JDBCClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

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

    @Override
    public Promise<Void> insertJo(String table, JsonObject jsonObject) {
        return null;
    }

    @Override
    public Promise<Void> insertJo(String table, List<JsonObject> sqlList) {
        return null;
    }

    @Override
    public Promise<Void> updateJo(String table, JsonObject jsonObject, String where) {
        return null;
    }

    @Override
    public Promise<Void> updateJo(List<UpdateTpl> sqlList) {
        return null;
    }
}
