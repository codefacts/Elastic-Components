package elasta.criteria.funcs;

import elasta.criteria.Func;import elasta.criteria.ParamsBuilder;

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
        return FuntionalOps.LP + operator + func.get(paramsBuilder) + FuntionalOps.RP;
    }
}
