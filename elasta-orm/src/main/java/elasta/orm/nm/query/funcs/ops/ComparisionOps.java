package elasta.orm.nm.query.funcs.ops;

import elasta.orm.nm.query.Func;
import elasta.orm.nm.query.funcs.FnCnst;
import elasta.orm.nm.query.funcs.Operation2;

/**
 * Created by Jango on 2017-01-07.
 */
public interface ComparisionOps {

    default Func eq(Func func1, Func func2) {
        return paramsBuilder -> new Operation2(FnCnst.EQ, func1, func2).get(paramsBuilder);
    }

    default Func ne(Func func1, Func func2) {
        return paramsBuilder -> new Operation2(FnCnst.NE, func1, func2).get(paramsBuilder);
    }

    default Func lt(Func func1, Func func2) {
        return paramsBuilder -> new Operation2(FnCnst.LT, func1, func2).get(paramsBuilder);
    }

    default Func lte(Func func1, Func func2) {
        return paramsBuilder -> new Operation2(FnCnst.LTE, func1, func2).get(paramsBuilder);
    }

    default Func gt(Func func1, Func func2) {
        return paramsBuilder -> new Operation2(FnCnst.GT, func1, func2).get(paramsBuilder);
    }

    default Func gte(Func func1, Func func2) {
        return paramsBuilder -> new Operation2(FnCnst.GTE, func1, func2).get(paramsBuilder);
    }
}
