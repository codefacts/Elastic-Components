package elasta.sql;

import elasta.sql.core.*;
import elasta.sql.impl.SqlListAndParamsList;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    SqlListAndParamsList deleteSql(Collection<DeleteData> deleteDataList);

    SqlAndParams existSql(String table, String primaryKey, Collection<SqlCriteria> sqlCriterias);

    SqlAndParams deleteSql(DeleteData deleteData);

    SqlAndParams toSql(SqlQuery sqlQuery);

    SqlListAndParamsList toSql(Set<DeleteRelationData> deleteRelationDataSet);
}
