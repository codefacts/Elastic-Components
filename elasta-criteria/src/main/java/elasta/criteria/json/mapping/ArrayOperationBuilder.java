package elasta.criteria.json.mapping;

import elasta.criteria.Func;

/**
 * Created by Jango on 2017-01-07.
 */
@FunctionalInterface
public interface ArrayOperationBuilder {
    Func build(Func[] funcs);
}
