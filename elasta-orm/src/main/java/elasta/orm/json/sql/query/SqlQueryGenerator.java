package elasta.orm.json.sql.query;

import elasta.orm.json.core.FieldInfo;
import elasta.orm.json.sql.core.TblPathFields;

import java.util.List;

/**
 * Created by Jango on 9/15/2016.
 */
public interface SqlQueryGenerator {

    List<TblPathFields> translate(String table, List<FieldInfo> fields);

    String sqlSelect(String rootTable, List<TblPathFields> fields);

    String sqlFromAndJoin(String rootTable, List<TblPathFields> fields);

    String sql(String rootTable, List<FieldInfo> fields);
}
