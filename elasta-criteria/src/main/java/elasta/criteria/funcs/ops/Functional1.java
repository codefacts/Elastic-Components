package elasta.criteria.funcs.ops;

import elasta.criteria.Func;
import elasta.criteria.ParamsBuilder;

import java.util.Objects;

/**
 * Created by sohan on 5/8/2017.
 */
final public class Functional1 implements Func {
    final String functionName;
    final Func func;

    public Functional1(String functionName, Func func) {
        Objects.requireNonNull(functionName);
        Objects.requireNonNull(func);
        this.functionName = functionName;
        this.func = func;
    }

    @Override
    public String get(ParamsBuilder paramsBuilder) {
        return functionName + "(" + func.get(paramsBuilder) + ")";
    }
}
