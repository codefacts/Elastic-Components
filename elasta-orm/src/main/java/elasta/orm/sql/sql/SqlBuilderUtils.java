package elasta.orm.sql.sql;

import elasta.orm.nm.delete.DeleteData;
import elasta.orm.sql.sql.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.List;

/**
 * Created by Jango on 10/12/2016.
 */
public interface SqlBuilderUtils {

    SqlAndParams insertSql(String table, JsonObject jsonObject);

    SqlAndParams querySql(String table, Collection<String> columns, JsonObject whereCriteria);

    SqlAndParams querySql(
        Collection<SqlSelection> sqlSelections,
        SqlFrom sqlFrom,
        Collection<SqlJoin> sqlJoins,
        Collection<SqlCriteria> sqlCriterias
    );

    SqlAndParams updateSql(UpdateTpl updateTpl);

    SqlAndParams deleteSql(String table, JsonObject where);

    SqlAndParams deleteSql(Collection<DeleteData> deleteDataList);
}
