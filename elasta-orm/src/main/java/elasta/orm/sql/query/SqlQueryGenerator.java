package elasta.orm.sql.query;

import elasta.orm.core.FieldInfo;

import java.util.List;

/**
 * Created by Jango on 9/15/2016.
 */
public interface SqlQueryGenerator {
    String toSql(String rootTable, List<FieldInfo> fields);
}
