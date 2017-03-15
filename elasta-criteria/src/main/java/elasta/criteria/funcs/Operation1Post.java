package elasta.criteria.funcs;

import elasta.criteria.Func;import elasta.criteria.ParamsBuilder;import elasta.criteria.Func;
import elasta.criteria.ParamsBuilder;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-07.
 */
public class Operation1Post implements Func {
    final String operator;
    final Func func;

    public Operation1Post(String operator, Func func) {
        Objects.requireNonNull(operator);
        Objects.requireNonNull(func);
        this.operator = operator;
        this.func = func;
    }

    @Override
    public String get(ParamsBuilder paramsBuilder) {
        return FnCnst.LP + func.get(paramsBuilder) + operator + FnCnst.RP;
    }
}
