package elasta.orm.nm.criteria.json.mapping;

import elasta.orm.nm.criteria.Func;

/**
 * Created by Jango on 2017-01-07.
 */
@FunctionalInterface
public interface ArrayOperationBuilder {
    Func build(Func[] funcs);
}
