package elasta.orm;

import java.util.List;

/**
 * Created by Jango on 9/15/2016.
 */
public interface TableJoiner {
    List<JoinTuple> join(String rootAlias, List<TableJoinerSpec> tableJoinerSpecs);
}
