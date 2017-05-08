package elasta.criteria.funcs.ops;

import elasta.criteria.Func;

/**
 * Created by sohan on 5/8/2017.
 */
public interface FunctionalOps {
    default Func count(Func func) {
        return new Functional1("COUNT", func);
    }

    default Func distinct(Func func) {
        return new Functional1("DISTINCT", func);
    }
}
