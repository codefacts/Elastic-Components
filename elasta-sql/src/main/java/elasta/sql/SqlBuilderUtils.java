package elasta.sql;

import elasta.criteria.Func;
import elasta.sql.core.*;
import elasta.sql.impl.SqlListAndParamsList;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.List;

/**
 * Created by Jango on 10/12/2016.
 */
public interface SqlBuilderUtils {

    SqlAndParams insertSql(String table, JsonObject jsonObject);

    SqlAndParams updateSql(UpdateTpl updateTpl);

    SqlAndParams toSql(SqlQuery sqlQuery);

    List<Func> toSelectFuncs(Collection<SqlSelection> sqlSelections);

    Collection<JoinData> toJoinDatas(String alias, Collection<SqlJoin> sqlJoins);

    List<Func> toWhereFuncs(Collection<SqlCriteria> sqlCriterias);

    UpdateTpl toInsertUpdateTpl(String table, JsonObject jsonObject);

    Collection<UpdateTpl> toInsertUpdateTpls(String table, Collection<JsonObject> jsonObjects);

    UpdateTpl toDeleteUpdateTpl(String table, JsonObject where);

    List<Func> toSelectFuncs(String alias, Collection<String> selectedColumns);

    List<Func> toWhereFuncs2(String alias, JsonObject whereCriteria);

    List<Func> toWhereFuncs(String rootAlias, Collection<SqlCondition> sqlConditions);
}
