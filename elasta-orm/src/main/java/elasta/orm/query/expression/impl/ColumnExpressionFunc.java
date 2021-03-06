package elasta.orm.query.expression.impl;

import elasta.criteria.Func;import elasta.criteria.ParamsBuilder;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.PathAliasHandler;

import java.util.Objects;

/**
 * Created by Jango on 17/02/08.
 */
final public class ColumnExpressionFunc implements Func {
    final FieldExpression fieldExpression;
    final PathAliasHandler pathAliasHandler;

    public ColumnExpressionFunc(FieldExpression fieldExpression, PathAliasHandler pathAliasHandler) {
        Objects.requireNonNull(fieldExpression);
        Objects.requireNonNull(pathAliasHandler);
        this.fieldExpression = fieldExpression;
        this.pathAliasHandler = pathAliasHandler;
    }

    @Override
    public String get(ParamsBuilder paramsBuilder) {
        return pathAliasHandler.toAliasedColumn(fieldExpression);
    }
}
