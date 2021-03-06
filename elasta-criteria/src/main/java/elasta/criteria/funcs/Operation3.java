package elasta.criteria.funcs;

import elasta.criteria.Func;
import elasta.criteria.ParamsBuilder;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-07.
 */
public class Operation3 implements Func {
    final Func func1;
    final Func func2;
    final Func func3;
    final String operator1;
    final String operator2;

    public Operation3(String operator1, String operator2, Func func1, Func func2, Func func3) {
        this.func3 = func3;
        Objects.requireNonNull(operator1);
        Objects.requireNonNull(operator2);
        Objects.requireNonNull(func1);
        Objects.requireNonNull(func2);
        Objects.requireNonNull(func3);
        this.operator1 = operator1;
        this.operator2 = operator2;
        this.func1 = func1;
        this.func2 = func2;
    }

    @Override
    public String get(ParamsBuilder paramsBuilder) {
        return FuntionalOps.LP +
            func1.get(paramsBuilder) +
            FuntionalOps.SPACE +
            operator1 +
            FuntionalOps.SPACE +
            func2.get(paramsBuilder) +
            FuntionalOps.SPACE +
            operator2 +
            FuntionalOps.RP;
    }
}
