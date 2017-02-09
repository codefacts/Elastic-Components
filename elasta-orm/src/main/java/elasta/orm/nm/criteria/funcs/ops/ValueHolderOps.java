package elasta.orm.nm.criteria.funcs.ops;

import elasta.orm.nm.criteria.Func;

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
