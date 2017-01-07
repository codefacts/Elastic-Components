package elasta.orm.nm.query.funcs;

import elasta.orm.nm.query.Func;
import elasta.orm.nm.query.ParamsBuilder;
import elasta.orm.nm.query.funcs.FnCnst;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-07.
 */
final public class Operation1 implements Func {
    final String operator;
    final Func func;

    public Operation1(String operator, Func func) {
        Objects.requireNonNull(operator);
        Objects.requireNonNull(func);
        this.operator = operator;
        this.func = func;
    }

    @Override
    public String get(ParamsBuilder paramsBuilder) {
        return FnCnst.LP + operator + func.get(paramsBuilder) + FnCnst.RP;
    }
}
