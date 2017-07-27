package elasta.criteria;

import elasta.criteria.funcs.ArrayOperation;
import elasta.criteria.funcs.FuntionalOps;
import elasta.criteria.funcs.Operation1;
import elasta.criteria.funcs.Operation2;
import elasta.criteria.funcs.ops.Functional1;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by sohan on 6/26/2017.
 */
public interface Ops {

    static Func eq(Func func1, Func func2) {
        return paramsBuilder -> new Operation2(FuntionalOps.EQ, func1, func2).get(paramsBuilder);
    }

    static Func ne(Func func1, Func func2) {
        return paramsBuilder -> new Operation2(FuntionalOps.NE, func1, func2).get(paramsBuilder);
    }

    static Func lt(Func func1, Func func2) {
        return paramsBuilder -> new Operation2(FuntionalOps.LT, func1, func2).get(paramsBuilder);
    }

    static Func lte(Func func1, Func func2) {
        return paramsBuilder -> new Operation2(FuntionalOps.LTE, func1, func2).get(paramsBuilder);
    }

    static Func gt(Func func1, Func func2) {
        return paramsBuilder -> new Operation2(FuntionalOps.GT, func1, func2).get(paramsBuilder);
    }

    static Func gte(Func func1, Func func2) {
        return paramsBuilder -> new Operation2(FuntionalOps.GTE, func1, func2).get(paramsBuilder);
    }

    static Func count(Func func) {
        return new Functional1("COUNT", func);
    }

    static Func distinct(Func func) {
        return new Functional1("DISTINCT", func);
    }

    static Func and(Func... funcs) {
        return paramsBuilder -> new ArrayOperation(FuntionalOps.AND, funcs).get(paramsBuilder);
    }

    static Func or(Func... funcs) {
        return paramsBuilder -> new ArrayOperation(FuntionalOps.OR, funcs).get(paramsBuilder);
    }

    static Func not(Func func) {
        return paramsBuilder -> new Operation1(FuntionalOps.NOT, func).get(paramsBuilder);
    }

    static Func valueOf(String value) {
        return paramsBuilder -> paramsBuilder.add(value);
    }

    static Func valueOf(Number value) {
        return paramsBuilder -> paramsBuilder.add(value);
    }

    static Func valueOf(Boolean value) {
        return paramsBuilder -> paramsBuilder.add(value);
    }

    static Func valueOfObj(Object value) {
        if (value instanceof Number) {
            return valueOf((Number) value);
        } else if (value.getClass() == Boolean.class) {
            return valueOf((Boolean) value);
        } else if (value.getClass() == String.class) {
            return valueOf((String) value);
        } else {
            throw new IllegalArgumentException("value type '" + value.getClass() + "' is not supported");
        }
    }

    static Func in(Func func1, Func... funcs) {

        return paramsBuilder -> {

            final String fn1 = func1.get(paramsBuilder);

            String fns = Arrays.stream(funcs)
                .map(func -> func.get(paramsBuilder))
                .collect(Collectors.joining(", "));

            return FuntionalOps.LP + fn1 + " IN (" + fns + ")" + FuntionalOps.RP;
        };
    }

    static Func like(Func func1, Func func2) {
        return paramsBuilder -> {

            String field = func1.get(paramsBuilder);

            String value = func2.get(paramsBuilder);

            return FuntionalOps.LP + field + " like " + value + FuntionalOps.RP;
        };
    }

}
