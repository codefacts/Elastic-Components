package elasta.orm;

import java.util.List;

/**
 * Created by Jango on 9/15/2016.
 */
public interface SelectClauseGenerator {
    List<SelectTuple> toSelectClause(String rootTable, String rootTableAlias, List<String> fields);
}
