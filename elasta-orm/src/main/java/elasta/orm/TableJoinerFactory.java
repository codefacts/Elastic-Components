package elasta.orm;

import java.util.List;

/**
 * Created by Jango on 9/14/2016.
 */
public interface TableJoinerFactory {
    TableJoiner create(String rootTable, List<JoinSpec> joinSpecs);
}
