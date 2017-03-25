package elasta.sql.impl;

import elasta.commons.SimpleCounter;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.sql.SqlExecutor;
import elasta.vertxutils.VertxUtils;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/14/2017.
 */
final public class SqlExecutorImpl implements SqlExecutor {
    final JDBCClient jdbcClient;

    public SqlExecutorImpl(JDBCClient jdbcClient) {
        Objects.requireNonNull(jdbcClient);
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Promise<ResultSet> query(String sql) {
        System.out.println("Query => " + sql);
        return withConn(con -> Promises.exec(
            defer -> con.query(sql, VertxUtils.deferred(defer))));
    }

    @Override
    public Promise<ResultSet> query(String sql, JsonArray params) {
        System.out.println("Query => " + sql + " Params: " + params.encode());
        return withConn(con -> Promises.exec(
            defer -> con.queryWithParams(sql, params, VertxUtils.deferred(defer))));
    }

    @Override
    public <T> Promise<T> queryScalar(String sql) {
        System.out.println("Query => " + sql);
        return query(sql).map(resultSet -> (T) resultSet.getResults().get(0).getValue(0));
    }

    @Override
    public <T> Promise<T> queryScalar(String sql, JsonArray params) {
        System.out.println("Query => " + sql + " Params: " + params.encode());
        return query(sql, params).map(resultSet -> (T) resultSet.getResults().get(0).getValue(0));
    }

    @Override
    public Promise<Void> update(String sql) {
        System.out.println("Query => " + sql);
        return withConn(con -> Promises.exec(
            defer -> con.update(sql, VertxUtils.deferred(defer))).map(o -> null)
        );
    }

    @Override
    public Promise<Void> update(String sql, JsonArray params) {
        System.out.println("Query => " + sql + " Params: " + params.encode());
        return withConn(con -> Promises.exec(
            defer -> con.updateWithParams(sql, params, VertxUtils.deferred(defer))).map(o -> null)
        );
    }

    @Override
    public Promise<Void> update(List<String> sqlList) {
        System.out.println("Queries => " + "[\n" + sqlList.stream().collect(Collectors.joining("\n")) + "\n]");
        return execAndCommit(con -> Promises.exec(
            objectDefer -> con.batch(sqlList, VertxUtils.deferred(objectDefer))));
    }

    @Override
    public Promise<Void> update(List<String> sqlList, List<JsonArray> paramsList) {
        SimpleCounter simpleCounter = new SimpleCounter();
        System.out.println("Queries => " + "[\n" + sqlList.stream().map(s -> s + " Params: " + paramsList.get(simpleCounter.value++).encode()).collect(Collectors.joining("\n")) + "\n]");
        SimpleCounter counter = new SimpleCounter(0);
        return execAndCommit(
            con -> Promises.when(sqlList.stream()
                .map(sql -> Promises.exec(dfr -> con.updateWithParams(sql, paramsList.get(counter.value++), VertxUtils.deferred(dfr))))
                .collect(Collectors.toList())
            ).map(objects -> (Void) null));
    }

    private Promise<SQLConnection> conn() {
        return Promises.exec(defer -> {
            jdbcClient.getConnection(VertxUtils.deferred(defer));
        });
    }

    private <T> Promise<T> withConn(Function<SQLConnection, Promise<T>> function) {
        return conn()
            .mapP(
                con -> {
                    try {
                        return function.apply(con)
                            .cmp(signal -> con.close());
                    } catch (Exception e) {
                        con.close();
                        return Promises.error(e);
                    }
                }
            );
    }

    private Promise<Void> execAndCommit(Function<SQLConnection, Promise<Void>> function) {
        return conn()
            .thenP(con -> Promises.exec(voidDefer -> con.setAutoCommit(false, VertxUtils.deferred(voidDefer))))
            .mapP(
                con -> {
                    try {
                        return function.apply(con)
                            .thenP(aVoid -> Promises.exec(voidDefer -> con.commit(VertxUtils.deferred(voidDefer))))
                            .errP(e -> Promises.exec(voidDefer -> con.rollback(VertxUtils.deferred(voidDefer))))
                            .cmp(signal -> con.close());
                    } catch (Exception e) {
                        con.close();
                        return Promises.error(e);
                    }
                }
            );
    }
}
