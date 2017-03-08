package elasta.orm.json.sql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Jango on 10/12/2016.
 */
public interface SqlBuilderUtils {
    SqlAndParams insertSql(String table, JsonObject jsonObject);

    SqlAndParams updateSql(String table, JsonObject jsonObject, String where, JsonArray params);

    SqlAndParams deleteSql(String table, String where, JsonArray jsonArray);

    SqlAndParams querySql(String table, List<String> columns, JsonObject whereCriteria);

    SqlAndParams querySql(Collection<SqlSelection> sqlSelections, SqlFrom sqlFrom, Collection<SqlJoin> sqlJoins, Collection<SqlCriteria> sqlCriterias);
}
