package elasta.orm.nm.query.funcs.ops;

import elasta.orm.nm.query.Func;
import elasta.orm.nm.query.funcs.ArrayOperation;
import elasta.orm.nm.query.funcs.FnCnst;
import elasta.orm.nm.query.funcs.Operation1;

import java.util.List;

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
