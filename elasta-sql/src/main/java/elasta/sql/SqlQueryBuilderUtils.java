package elasta.sql;

import elasta.sql.core.SqlAndParams;
import elasta.sql.core.SqlQuery;
import elasta.sql.core.UpdateTpl;

/**
 * Created by sohan on 6/26/2017.
 */
public interface SqlQueryBuilderUtils {

    SqlAndParams updateSql(UpdateTpl updateTpl);

    SqlAndParams toSql(SqlQuery sqlQuery);
}
