package elasta.orm.json.sql;

import com.google.common.collect.ImmutableList;
import elasta.commons.SimpleCounter;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.vertxutils.StaticUtils;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Jango on 9/25/2016.
 */
public class DbSqlImpl implements DbSql {
    private final JDBCClient jdbcClient;
    private final SqlBuilderUtils sqlBuilderUtils;

    public DbSqlImpl(JDBCClient jdbcClient, SqlBuilderUtils sqlBuilderUtils) {
        this.jdbcClient = jdbcClient;
        this.sqlBuilderUtils = sqlBuilderUtils;
    }

    @Override
    public Promise<ResultSet> query(String sql) {
        return withConn(con -> Promises.exec(
            defer -> con.query(sql, StaticUtils.deferred(defer))));
    }

    @Override
    public Promise<ResultSet> query(String sql, JsonArray params) {
        return withConn(con -> Promises.exec(
            defer -> con.queryWithParams(sql, params, StaticUtils.deferred(defer))));
    }

    @Override
    public <T> Promise<T> queryScalar(String sql) {
        return query(sql).map(resultSet -> (T) resultSet.getResults().get(0).getValue(0));
    }

    @Override
    public <T> Promise<T> queryScalar(String sql, JsonArray params) {
        return query(sql, params).map(resultSet -> (T) resultSet.getResults().get(0).getValue(0));
    }

    @Override
    public Promise<Void> update(String sql) {
        return withConn(con -> Promises.exec(
            defer -> con.update(sql, StaticUtils.deferred(defer))));
    }

    @Override
    public Promise<Void> update(String sql, JsonArray params) {
        return withConn(con -> Promises.exec(
            defer -> con.updateWithParams(sql, params, StaticUtils.deferred(defer))));
    }

    @Override
    public Promise<Void> update(List<String> sqlList) {
        return execAndCommit(con -> Promises.exec(objectDefer -> {
            con.batch(sqlList, StaticUtils.deferred(objectDefer));
        }));
    }

    @Override
    public Promise<Void> update(List<String> sqlList, List<JsonArray> paramsList) {
        SimpleCounter counter = new SimpleCounter(0);
        return execAndCommit(
            con -> Promises.when(sqlList.stream()
                .map(sql -> Promises.exec(dfr -> con.updateWithParams(sql, paramsList.get(counter.value++), StaticUtils.deferred(dfr))))
                .collect(Collectors.toList())
            ).map(objects -> (Void) null));
    }

    @Override
    public Promise<Void> insertJo(String table, JsonObject jsonObject) {
        SqlAndParams sqlAndParams = sqlBuilderUtils.insertSql(table, jsonObject);
        return update(sqlAndParams.getSql(), sqlAndParams.getParams());
    }

    @Override
    public Promise<Void> insertJo(String table, List<JsonObject> sqlList) {
        ImmutableList.Builder<String> sqlListBuilder = ImmutableList.builder();
        ImmutableList.Builder<JsonArray> paramsListBuilder = ImmutableList.builder();

        sqlList.stream().map(val -> sqlBuilderUtils.insertSql(table, val))
            .forEach(insertSql -> {
                sqlListBuilder.add(insertSql.getSql());
                paramsListBuilder.add(insertSql.getParams());
            });

        return update(sqlListBuilder.build(), paramsListBuilder.build());
    }

    @Override
    public Promise<Void> updateJo(String table, JsonObject jsonObject, String where, JsonArray params) {
        SqlAndParams sqlAndParams = sqlBuilderUtils.updateSql(table, jsonObject, where, params);
        return update(sqlAndParams.getSql(), sqlAndParams.getParams());
    }

    @Override
    public Promise<Void> updateJo(List<UpdateTpl> sqlList) {
        return execAndCommit(con -> Promises.when(
            sqlList.stream().map(

                updateTpl -> {

                    UpdateOperationType operationType = updateTpl.getUpdateOperationType();

                    SqlAndParams sqlAndParams;

                    if (operationType == UpdateOperationType.INSERT) {

                        sqlAndParams = sqlBuilderUtils.insertSql(updateTpl.getTable(), updateTpl.getData());

                    } else if (operationType == UpdateOperationType.UPDATE) {

                        sqlAndParams = sqlBuilderUtils.updateSql(updateTpl.getTable(), updateTpl.getData(), updateTpl.getWhere(), updateTpl.getJsonArray());

                    } else {

                        sqlAndParams = sqlBuilderUtils.deleteSql(updateTpl.getTable(), updateTpl.getWhere(), updateTpl.getJsonArray());
                    }

                    return Promises.exec(objectDefer -> {
                        con.updateWithParams(sqlAndParams.getSql(), sqlAndParams.getParams(), StaticUtils.deferred(objectDefer));
                    });
                }).collect(Collectors.toList())

        ).map(objects -> (Void) null));
    }

    private Promise<SQLConnection> conn() {
        return Promises.exec(defer -> {
            jdbcClient.getConnection(StaticUtils.deferred(defer));
        });
    }

    private <T> Promise<T> withConn(Function<SQLConnection, Promise<T>> function) {
        return conn()
            .mapP(
                con -> {
                    try {
                        return function.apply(con)
                            .errP(e -> Promises.exec(voidDefer -> con.rollback(StaticUtils.deferred(voidDefer))))
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
            .thenP(con -> Promises.exec(voidDefer -> con.setAutoCommit(false, StaticUtils.deferred(voidDefer))))
            .mapP(
                con -> {
                    try {
                        return function.apply(con)
                            .thenP(aVoid -> Promises.exec(voidDefer -> con.commit(StaticUtils.deferred(voidDefer))))
                            .errP(e -> Promises.exec(voidDefer -> con.rollback(StaticUtils.deferred(voidDefer))))
                            .cmp(signal -> con.close());
                    } catch (Exception e) {
                        con.close();
                        return Promises.error(e);
                    }
                }
            );
    }

    public static void main(String[] arg) {
        System.out.println("ok");
    }
}
