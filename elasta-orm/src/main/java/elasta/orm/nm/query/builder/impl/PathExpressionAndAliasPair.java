package elasta.orm.nm.query.builder.impl;

import elasta.orm.sql.sql.core.JoinType;
import elasta.orm.nm.query.PathExpression;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by Jango on 17/02/09.
 */
final public class PathExpressionAndAliasPair {
    final PathExpression pathExpression;
    final String alias;
    final Optional<JoinType> joinType;

    public PathExpressionAndAliasPair(PathExpression pathExpression, String alias, Optional<JoinType> joinType) {
        Objects.requireNonNull(pathExpression);
        Objects.requireNonNull(alias);
        Objects.requireNonNull(joinType);
        this.pathExpression = pathExpression;
        this.alias = alias;
        this.joinType = joinType;
    }

    public Optional<JoinType> getJoinType() {
        return joinType;
    }

    public PathExpression getPathExpression() {
        return pathExpression;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public String toString() {
        return "PathExpressionAndAliasPair{" +
            "pathExpression=" + pathExpression +
            ", alias='" + alias + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PathExpressionAndAliasPair that = (PathExpressionAndAliasPair) o;

        if (pathExpression != null ? !pathExpression.equals(that.pathExpression) : that.pathExpression != null)
            return false;
        return alias != null ? alias.equals(that.alias) : that.alias == null;

    }

    @Override
    public int hashCode() {
        int result = pathExpression != null ? pathExpression.hashCode() : 0;
        result = 31 * result + (alias != null ? alias.hashCode() : 0);
        return result;
    }
}
