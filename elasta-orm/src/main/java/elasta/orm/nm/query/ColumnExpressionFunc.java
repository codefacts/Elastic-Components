package elasta.orm.nm.query;

import elasta.orm.nm.criteria.Func;
import elasta.orm.nm.criteria.ParamsBuilder;

import java.util.Objects;

/**
 * Created by Jango on 17/02/08.
 */
final public class ColumnExpressionFunc implements Func {
    final ColumnExpression columnExpression;
    final PathAliasHandler pathAliasHandler;

    public ColumnExpressionFunc(ColumnExpression columnExpression, PathAliasHandler pathAliasHandler) {
        Objects.requireNonNull(columnExpression);
        Objects.requireNonNull(pathAliasHandler);
        this.columnExpression = columnExpression;
        this.pathAliasHandler = pathAliasHandler;
    }

    @Override
    public String get(ParamsBuilder paramsBuilder) {
        return pathAliasHandler.toAliasedColumn(columnExpression);
    }
}
