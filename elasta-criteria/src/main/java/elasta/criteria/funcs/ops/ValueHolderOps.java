package elasta.criteria.funcs.ops;

import elasta.criteria.Func;import elasta.criteria.Func;

/**
 * Created by Jango on 2017-01-07.
 */
public interface ValueHolderOps {

    default Func valueOf(String value) {
        return paramsBuilder -> paramsBuilder.add(value);
    }

    default Func valueOf(Number value) {
        return paramsBuilder -> paramsBuilder.add(value);
    }

    default Func valueOf(Boolean value) {
        return paramsBuilder -> paramsBuilder.add(value);
    }
}
