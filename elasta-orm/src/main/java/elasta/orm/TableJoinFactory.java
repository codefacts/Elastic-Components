package elasta.orm;

import java.util.Collection;
import java.util.List;

/**
 * Created by Jango on 9/15/2016.
 */
public interface TableJoinFactory {

    TableJoin create(RootTuple root, Collection<JoinTuple> joinTuples);
}
