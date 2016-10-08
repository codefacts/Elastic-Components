package elasta.orm;

import elasta.core.promise.intfs.Promise;
import elasta.orm.json.core.FieldInfo;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by Jango on 9/14/2016.
 */
public interface Db {

    <T> Promise<JsonObject> findOne(String model, T id);

    <T> Promise<JsonObject> findOne(String model, T id, List<FieldInfo> selectFields);

    <T> Promise<List<JsonObject>> findAll(String model, List<T> ids);

    <T> Promise<List<JsonObject>> findAll(String model, List<T> ids, List<FieldInfo> selectFields);

    Promise<JsonObject> insertOrUpdate(String model, JsonObject data);

    Promise<List<JsonObject>> insertOrUpdateAll(String model, List<JsonObject> jsonObjects);

    <T> Promise<T> delete(String model, T id);

    <T> Promise<List<T>> deleteAll(String model, List<T> ids);

    Promise<Long> count(String model);

    Promise<Long> count(String model, JsonObject criteria);

    Promise<List<JsonObject>> findAll(String model, JsonObject criteria);

    Promise<List<JsonObject>> findAll(String model, JsonObject criteria, List<FieldInfo> selectFields);

}
