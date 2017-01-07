package elasta.orm.nm.query.funcs.ops;

import elasta.orm.nm.query.Func;

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
