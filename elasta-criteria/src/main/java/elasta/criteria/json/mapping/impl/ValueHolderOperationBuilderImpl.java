package elasta.criteria.json.mapping.impl;

import elasta.criteria.Func;
import elasta.criteria.funcs.ops.Ops;
import elasta.criteria.json.mapping.ValueHolderOperationBuilder;
import elasta.criteria.json.mapping.ex.ValueHolderException;

/**
 * Created by Jango on 2017-01-07.
 */
final public class ValueHolderOperationBuilderImpl implements ValueHolderOperationBuilder {

    @Override
    public Func build(Object value) {

        if (value == null) {
            throw new ValueHolderException("Null value is not supported");
        }

        if (value.getClass() == String.class) {
            return Ops.valueOf(value.toString());
        }

        if (value instanceof Number) {
            return Ops.valueOf((Number) value);
        }

        if (value.getClass() == Boolean.class) {
            return Ops.valueOf((Boolean) value);
        }

        throw new ValueHolderException("Value type '" + value.getClass() + "' is not supported.");
    }
}
