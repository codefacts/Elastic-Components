package elasta.orm;

import java.util.List;
import java.util.Map;

/**
 * Created by Jango on 9/14/2016.
 */
public interface TableJoinerFactory {
    TableJoiner create(Map<String, JoinSpec> joinSpecsByColumnMap);
}
