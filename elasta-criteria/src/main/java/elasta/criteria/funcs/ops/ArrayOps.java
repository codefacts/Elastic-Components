package elasta.criteria.funcs.ops;

import elasta.criteria.Func;
import elasta.criteria.funcs.FnCnst;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by sohan on 5/8/2017.
 */
public interface ArrayOps {

    default Func in(Func func1, Func... funcs) {

        return paramsBuilder -> {

            final String fn1 = func1.get(paramsBuilder);
            
            String fns = Arrays.stream(funcs)
                .map(func -> func.get(paramsBuilder))
                .collect(Collectors.joining(", "));

            return FnCnst.LP + fn1 + " IN (" + fns + ")" + FnCnst.RP;
        };
    }
}
