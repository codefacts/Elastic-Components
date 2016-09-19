package elasta.orm;

import java.util.List;

/**
 * Created by Jango on 9/15/2016.
 */
public interface SqlQueryGenerator {
    String toSql(String rootTable, List<String> fields);
}
