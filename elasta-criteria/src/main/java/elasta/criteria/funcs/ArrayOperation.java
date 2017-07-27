package elasta.criteria.funcs;

import elasta.criteria.Func;
import elasta.criteria.ParamsBuilder;
import elasta.criteria.ex.ArrayOperationException;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-07.
 */
final public class ArrayOperation implements Func {
    final String operation;
    final Func[] funcs;

    public ArrayOperation(String operation, Func[] funcs) {
        Objects.requireNonNull(operation);
        Objects.requireNonNull(funcs);
        if (funcs.length < 2) {
            throw new ArrayOperationException("Number of arguments must be greater than 1. Number found is " + funcs.length);
        }
        this.operation = operation;
        this.funcs = funcs;
    }

    @Override
    public String get(ParamsBuilder paramsBuilder) {

        if (funcs.length <= 0) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        final String _op_ = FuntionalOps.SPACE + operation + FuntionalOps.SPACE;

        stringBuilder.append(FuntionalOps.LP);

        for (Func func : funcs) {
            stringBuilder.append(func.get(paramsBuilder)).append(_op_);
        }

        stringBuilder.delete(stringBuilder.length() - _op_.length(), stringBuilder.length());

        stringBuilder.append(FuntionalOps.RP);

        return stringBuilder.toString();
    }
}
