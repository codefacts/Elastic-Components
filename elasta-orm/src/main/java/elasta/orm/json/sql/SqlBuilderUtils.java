package elasta.orm.json.sql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 10/12/2016.
 */
public interface SqlBuilderUtils {
    SqlAndParams insertSql(String table, JsonObject jsonObject);

    SqlAndParams updateSql(String table, JsonObject jsonObject, String where, JsonArray params);

    SqlAndParams deleteSql(String table, String where, JsonArray jsonArray);
}
