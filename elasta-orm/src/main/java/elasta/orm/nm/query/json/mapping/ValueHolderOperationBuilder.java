package elasta.orm.nm.query.json.mapping;

import elasta.orm.nm.query.Func;

/**
 * Created by Jango on 2017-01-07.
 */
@FunctionalInterface
public interface ValueHolderOperationBuilder {
    Func build(Object value);
}
