package elasta.criteria.funcs;

import elasta.criteria.Func;
import elasta.criteria.ParamsBuilder;

import java.util.Objects;

/**
 * Created by sohan on 6/26/2017.
 */
final public class FieldFunc implements Func {
    final String field;

    public FieldFunc(String field) {
        Objects.requireNonNull(field);
        this.field = field;
    }

    @Override
    public String get(ParamsBuilder paramsBuilder) {
        return field;
    }
}
