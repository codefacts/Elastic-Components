package elasta.vertxutils;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by Jango on 9/14/2016.
 */
public class DbUtilsImpl implements DbUtils {
    @Override
    public <T> Promise<JsonObject> findOne(String table, T id) {
        return null;
    }

    @Override
    public <T> Promise<JsonObject> findOne(String table, T id, List<String> selectFields) {
        return null;
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
}
