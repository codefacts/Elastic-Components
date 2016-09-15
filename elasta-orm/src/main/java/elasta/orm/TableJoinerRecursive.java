package elasta.orm;

import java.util.List;
import java.util.Map;

/**
 * Created by Jango on 9/15/2016.
 */
public interface TableJoinerRecursive {

    List<JoinTuple> joinRecursive(String rootTable, List<List<String>> joinColumns);
}
