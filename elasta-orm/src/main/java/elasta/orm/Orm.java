package elasta.orm;

import elasta.core.promise.intfs.Promise;
import elasta.sql.core.FieldInfo;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by Jango on 9/14/2016.
 */
public interface Orm {

    <T> Promise<JsonObject> findOne(String entity, T id);

    <T> Promise<JsonObject> findOne(String entity, T id, List<FieldInfo> selectFields);

    <T> Promise<List<JsonObject>> findAll(String entity, List<T> ids);

    <T> Promise<List<JsonObject>> findAll(String entity, List<T> ids, List<FieldInfo> selectFields);

    Promise<JsonObject> upsert(String entity, JsonObject data);

    Promise<List<JsonObject>> upsertAll(String entity, List<JsonObject> jsonObjects);

    <T> Promise<T> delete(String entity, T id);

    <T> Promise<List<T>> deleteAll(String entity, List<T> ids);

    Promise<Long> count(String entity);

    Promise<Long> count(String entity, JsonObject criteria);

    Promise<List<JsonObject>> findAll(String entity, JsonObject criteria);

    Promise<List<JsonObject>> findAll(String entity, JsonObject criteria, List<FieldInfo> selectFields);

}
