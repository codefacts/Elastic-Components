package elasta.orm.nm.query.impl;

import elasta.orm.nm.query.FieldExpression;
import elasta.orm.nm.query.PathExpression;
import elasta.orm.nm.query.impl.ex.FieldExpressionException;

/**
 * Created by Jango on 17/02/10.
 */
final public class FieldExpressionImpl implements FieldExpression {
    final PathExpression pathExpression;

    public FieldExpressionImpl(PathExpression pathExpression) {
        if (pathExpression.size() < 2) {
            throw new FieldExpressionException("At least 2 elements expected in path expression but less element found in " + pathExpression);
        }
        this.pathExpression = pathExpression;
    }

    public FieldExpressionImpl(String pathExpression) {
        this.pathExpression = new PathExpressionImpl(pathExpression);
        if (this.pathExpression.size() < 2) {
            throw new FieldExpressionException("At least 2 elements expected in path expression but less element found in " + pathExpression);
        }
    }

    @Override
    public PathExpression getParentPath() {
        return pathExpression.getParent().get();
    }

    @Override
    public String getField() {
        return pathExpression.last();
    }

    @Override
    public PathExpression toPathExpression() {
        return pathExpression;
    }
}
