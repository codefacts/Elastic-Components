package elasta.orm.nm.query.json.mapping;

import elasta.orm.nm.query.Func;
import elasta.orm.nm.query.funcs.ops.ValueHolderOps;
import elasta.orm.nm.query.json.mapping.ex.ValueHolderException;

/**
 * Created by Jango on 2017-01-07.
 */
public class ValueHolderOperationBuilderImpl implements ValueHolderOperationBuilder {
    final ValueHolderOps valueHolderOps;

    public ValueHolderOperationBuilderImpl(ValueHolderOps valueHolderOps) {
        this.valueHolderOps = valueHolderOps;
    }

    @Override
    public Func build(Object value) {

        if (value == null) {
            throw new ValueHolderException("Null value is not supported");
        }

        if (value.getClass() == String.class) {
            return valueHolderOps.valueOf(value.toString());
        }

        if (value instanceof Number) {
            return valueHolderOps.valueOf((Number) value);
        }

        if (value.getClass() == Boolean.class) {
            return valueHolderOps.valueOf((Boolean) value);
        }

        throw new ValueHolderException("Value type '" + value.getClass() + "' is not supported.");
    }
}
