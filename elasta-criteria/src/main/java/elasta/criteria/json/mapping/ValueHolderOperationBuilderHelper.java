package elasta.criteria.json.mapping;

import elasta.criteria.Func;import elasta.criteria.Func;

/**
 * Created by Jango on 2017-01-07.
 */
@FunctionalInterface
public interface ValueHolderOperationBuilderHelper {
    Func build(Object value);
}
