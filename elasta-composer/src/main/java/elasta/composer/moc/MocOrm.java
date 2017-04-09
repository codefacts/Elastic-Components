package elasta.composer.moc;

import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.Orm;
import elasta.sql.core.FieldInfo;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by Jango on 11/13/2016.
 */
public class MocOrm implements Orm {
    @Override
    public <T> Promise<JsonObject> findOne(String entity, T id) {
        return null;
    }

    @Override
    public <T> Promise<JsonObject> findOne(String entity, T id, List<FieldInfo> selectFields) {
        return null;
    }

    @Override
    public <T> Promise<List<JsonObject>> findAll(String entity, List<T> ids) {
        return null;
    }

    @Override
    public <T> Promise<List<JsonObject>> findAll(String entity, List<T> ids, List<FieldInfo> selectFields) {
        return null;
    }

    @Override
    public Promise<JsonObject> upsert(String entity, JsonObject data) {
        return null;
    }

    @Override
    public Promise<List<JsonObject>> upsertAll(String entity, List<JsonObject> jsonObjects) {
        return null;
    }

    @Override
    public <T> Promise<T> delete(String entity, T id) {
        return null;
    }

    @Override
    public <T> Promise<List<T>> deleteAll(String entity, List<T> ids) {
        return null;
    }

    @Override
    public Promise<Long> count(String entity) {
        return null;
    }

    @Override
    public Promise<Long> count(String entity, JsonObject criteria) {
        return null;
    }

    @Override
    public Promise<List<JsonObject>> findAll(String entity, JsonObject criteria) {
        return null;
    }

    @Override
    public Promise<List<JsonObject>> findAll(String entity, JsonObject criteria, List<FieldInfo> selectFields) {
        return null;
    }
}
