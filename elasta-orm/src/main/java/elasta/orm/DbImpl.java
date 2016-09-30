package elasta.orm;

import elasta.core.intfs.FunctionUnchecked;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Defer;
import elasta.core.promise.intfs.Promise;
import elasta.orm.exceptions.NoResultException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;

import java.util.Collections;
import java.util.List;

/**
 * Created by Jango on 9/14/2016.
 */
final public class DbImpl implements Db {
    private final JDBCClient jdbcClient;
    private final String ID = "id";

    public DbImpl(JDBCClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public <T> Promise<JsonObject> findOne(String table, T id) {
        return execDB(con -> {
            return Promises.withDefer(defer -> {
                con.queryWithParams(
                    "select * from " + table +
                        " where " + ID + " = ?", new JsonArray(Collections.singletonList(id)), rr -> {
                        if (rr.failed()) {
                            defer.reject(rr.cause());
                        } else {
                            List<JsonObject> rows = rr.result().getRows();
                            if (rows.size() <= 0) {
                                defer.reject(new NoResultException("No result was found for table: '" + table + "' and id: " + id));
                            } else {
                                defer.resolve(rows.get(0));
                            }
                        }
                    });
            });
        });
    }

    @Override
    public <T> Promise<JsonObject> findOne(String table, T id, List<String> selectFields) {
        return execDB(con -> {
            return Promises.withDefer(defer -> {
                con.queryWithParams(
                    "select " + String.join(", ", selectFields) + " from " + table +
                        " where " + ID + " = ?", new JsonArray(Collections.singletonList(id)), rr -> {
                        if (rr.failed()) {
                            defer.reject(rr.cause());
                        } else {
                            List<JsonObject> rows = rr.result().getRows();
                            if (rows.size() <= 0) {
                                defer.reject(new NoResultException("No result was found for table: '" + table + "' and id: " + id));
                            } else {
                                defer.resolve(rows.get(0));
                            }
                        }
                    });
            });
        });
    }

    @Override
    public <T> Promise<List<JsonObject>> findAll(String table, List<T> ids) {
        return null;
    }

    @Override
    public <T> Promise<List<JsonObject>> findAll(String table, List<T> ids, List<String> selectFields) {
        return null;
    }

    @Override
    public Promise<Long> count(String table) {
        return null;
    }

    @Override
    public <T> Promise<T> create(String table, JsonObject data) {
        return null;
    }

    @Override
    public Promise<JsonObject> update(String table, JsonObject data) {
        return null;
    }

    @Override
    public <T> Promise<T> delete(String table, T id) {
        return null;
    }

    @Override
    public <T> Promise<List<T>> createAll(String table, List<JsonObject> jsonObjects) {
        return null;
    }

    @Override
    public Promise<List<JsonObject>> updateAll(String table, List<JsonObject> jsonObjects) {
        return null;
    }

    @Override
    public <T> Promise<List<T>> deleteAll(String table, List<T> ids) {
        return null;
    }

    @Override
    public Promise<Long> count(String table, JsonObject criteria) {
        return null;
    }

    @Override
    public Promise<List<JsonObject>> findAll(String table, JsonObject criteria) {
        return null;
    }

    @Override
    public Promise<List<JsonObject>> findAll(String table, JsonObject criteria, List<String> selectFields) {
        return null;
    }

    private <T> Promise<T> execDB(FunctionUnchecked<SQLConnection, Promise<T>> functionUnchecked) {
        Defer<T> defer = Promises.defer();
        jdbcClient.getConnection(result -> {
            if (result.failed()) {
                defer.reject(result.cause());
            } else {
                try {
                    SQLConnection connection = result.result();

                    functionUnchecked.apply(connection)
                        .cmp(signal -> {
                            connection.close();
                            if (signal.isError()) {
                                defer.reject(signal.err());
                            } else {
                                defer.resolve(signal.val());
                            }
                        })
                    ;

                } catch (Throwable throwable) {
                    defer.reject(throwable);
                }
            }
        });
        return defer.promise();
    }
}
