package elasta.orm.query.expression.impl;

import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.expression.impl.ex.FieldExpressionException;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldExpressionImpl that = (FieldExpressionImpl) o;

        return pathExpression != null ? pathExpression.equals(that.pathExpression) : that.pathExpression == null;

    }

    @Override
    public int hashCode() {
        return pathExpression != null ? pathExpression.hashCode() : 0;
    }

    @Override
    public String toString() {
        return pathExpression.toString();
    }
}
