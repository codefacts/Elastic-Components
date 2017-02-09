package elasta.orm.nm.criteria.funcs.ops;

import elasta.orm.nm.criteria.Func;
import elasta.orm.nm.criteria.funcs.ArrayOperation;
import elasta.orm.nm.criteria.funcs.FnCnst;
import elasta.orm.nm.criteria.funcs.Operation1;

/**
 * Created by Jango on 2017-01-07.
 */
public interface LogicalOps {
    //Logical Operators
    default Func and(Func... funcs) {
        return paramsBuilder -> new ArrayOperation(FnCnst.AND, funcs).get(paramsBuilder);
    }

    default Func or(Func... funcs) {
        return paramsBuilder -> new ArrayOperation(FnCnst.OR, funcs).get(paramsBuilder);
    }

    default Func not(Func func) {
        return paramsBuilder -> new Operation1(FnCnst.NOT, func).get(paramsBuilder);
    }
}
