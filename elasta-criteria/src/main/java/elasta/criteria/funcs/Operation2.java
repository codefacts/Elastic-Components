package elasta.criteria.funcs;

import elasta.criteria.Func;
import elasta.criteria.ParamsBuilder;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-07.
 */
final public class Operation2 implements Func {
    final Func func1;
    final Func func2;
    final String operator;

    public Operation2(String operator, Func func1, Func func2) {
        Objects.requireNonNull(operator);
        Objects.requireNonNull(func1);
        Objects.requireNonNull(func2);
        this.operator = operator;
        this.func1 = func1;
        this.func2 = func2;
    }

    @Override
    public String get(ParamsBuilder paramsBuilder) {
        return FuntionalOps.LP + func1.get(paramsBuilder) + FuntionalOps.SPACE + operator + FuntionalOps.SPACE + func2.get(paramsBuilder) + FuntionalOps.RP;
    }
}
