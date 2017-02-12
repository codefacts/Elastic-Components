package elasta.orm.nm.query;

import elasta.orm.nm.query.impl.PathExpressionImpl;

import java.util.Optional;

/**
 * Created by Jango on 17/02/09.
 */
public interface PathExpression {

    Optional<PathExpression> getParent();

    String[] parts();

    String getAt(int index);

    int size();

    String root();

    String last();

    static PathExpression create(String... parts) {
        return new PathExpressionImpl(parts);
    }

    PathExpression subPath(int startIndex, int newSize);
}
