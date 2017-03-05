package elasta.orm.json.sql;

import elasta.core.promise.intfs.Promise;
import elasta.orm.nm.delete.DeleteData;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.util.Collection;
import java.util.List;

/**
 * Created by Jango on 9/25/2016.
 */
public interface DbSql {

    Promise<ResultSet> query(String sql);

    Promise<ResultSet> query(String sql, JsonArray params);

    <T> Promise<T> queryScalar(String sql);

    <T> Promise<T> queryScalar(String sql, JsonArray params);

    Promise<Void> update(String sql);

    Promise<Void> update(String sql, JsonArray params);

    Promise<Void> update(List<String> sqlList);

    Promise<Void> update(List<String> sqlList, List<JsonArray> paramsList);

    Promise<Void> insertJo(String table, JsonObject jsonObject);

    Promise<Void> insertJo(String table, List<JsonObject> sqlList);

    Promise<Void> updateJo(String table, JsonObject jsonObject, String where, JsonArray params);

    Promise<Void> updateJo(List<UpdateTpl> sqlList);

    Promise<Void> delete(String table, JsonObject where);

    Promise<Void> delete(Collection<DeleteData> deleteDataList);

    Promise<List<JsonObject>> queryWhere(String table, List<String> columns, JsonObject whereCriteria);
}
