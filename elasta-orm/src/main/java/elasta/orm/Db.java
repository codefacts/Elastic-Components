package elasta.orm;

import com.google.common.collect.ImmutableMap;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;

/**
 * Created by Jango on 9/14/2016.
 */
public interface DB {

    <T> Promise<JsonObject> findOne(String table, T id);

    <T> Promise<JsonObject> findOne(String table, T id, List<String> selectFields);

    <T> Promise<List<JsonObject>> findAll(String table, List<T> ids);

    <T> Promise<List<JsonObject>> findAll(String table, List<T> ids, List<String> selectFields);

    Promise<Long> count(String table);

    <T> Promise<T> create(String table, JsonObject data);

    Promise<JsonObject> update(String table, JsonObject data);

    <T> Promise<T> delete(String table, T id);

    <T> Promise<List<T>> createAll(String table, List<JsonObject> jsonObjects);

    Promise<List<JsonObject>> updateAll(String table, List<JsonObject> jsonObjects);

    <T> Promise<List<T>> deleteAll(String table, List<T> ids);

    public Promise<Long> count(String table, JsonObject criteria);

    Promise<List<JsonObject>> findAll(String table, JsonObject criteria);

    Promise<List<JsonObject>> findAll(String table, JsonObject criteria, List<String> selectFields);

}
