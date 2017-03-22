package elasta.orm.query.expression.impl;

import elasta.criteria.ParamsBuilder;
import elasta.orm.query.expression.builder.FieldExpressionHolderFunc;

import java.util.Objects;

/**
 * Created by Jango on 17/02/11.
 */
final public class FieldExpressionHolderFuncImpl2 implements FieldExpressionHolderFunc {
    final String aliasedColumn;

    public FieldExpressionHolderFuncImpl2(String aliasedColumn) {
        Objects.requireNonNull(aliasedColumn);
        this.aliasedColumn = aliasedColumn;
    }

    @Override
    public String get(ParamsBuilder paramsBuilder) {
        return aliasedColumn;
    }
}
