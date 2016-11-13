package elasta.composer.moc;

import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.json.core.FieldInfo;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by Jango on 11/13/2016.
 */
public class MocDb implements elasta.orm.Db {
    @Override
    public <T> Promise<JsonObject> findOne(String model, T id) {
        return Promises.empty();
    }

    @Override
    public <T> Promise<JsonObject> findOne(String model, T id, List<FieldInfo> selectFields) {
        return Promises.empty();
    }

    @Override
    public <T> Promise<List<JsonObject>> findAll(String model, List<T> ids) {
        return Promises.empty();
    }

    @Override
    public <T> Promise<List<JsonObject>> findAll(String model, List<T> ids, List<FieldInfo> selectFields) {
        return Promises.empty();
    }

    @Override
    public Promise<JsonObject> insertOrUpdate(String model, JsonObject data) {
        return Promises.empty();
    }

    @Override
    public Promise<List<JsonObject>> insertOrUpdateAll(String model, List<JsonObject> jsonObjects) {
        return Promises.empty();
    }

    @Override
    public <T> Promise<T> delete(String model, T id) {
        return Promises.empty();
    }

    @Override
    public <T> Promise<List<T>> deleteAll(String model, List<T> ids) {
        return Promises.empty();
    }

    @Override
    public Promise<Long> count(String model) {
        return Promises.empty();
    }

    @Override
    public Promise<Long> count(String model, JsonObject criteria) {
        return Promises.empty();
    }

    @Override
    public Promise<List<JsonObject>> findAll(String model, JsonObject criteria) {
        return Promises.empty();
    }

    @Override
    public Promise<List<JsonObject>> findAll(String model, JsonObject criteria, List<FieldInfo> selectFields) {
        return Promises.empty();
    }
}
