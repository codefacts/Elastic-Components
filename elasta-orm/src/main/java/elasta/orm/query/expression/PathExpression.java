package elasta.orm.query.expression;

import com.google.common.collect.ImmutableList;
import elasta.orm.query.expression.impl.PathExpressionImpl;

import java.util.List;
import java.util.Optional;

/**
 * Created by Jango on 17/02/09.
 */
public interface PathExpression {

    Optional<PathExpression> getParent();

    List<String> parts();

    String getAt(int index);

    int size();

    String root();

    String last();

    static PathExpression create(String... parts) {
        return new PathExpressionImpl(ImmutableList.copyOf(parts));
    }

    static PathExpression create(List<String>... partsList) {
        ImmutableList.Builder<String> listBuilder = ImmutableList.builder();
        for (List<String> parts : partsList) {
            listBuilder.addAll(parts);
        }
        return new PathExpressionImpl(listBuilder.build());
    }

    static PathExpression create(String[]... partsList) {
        ImmutableList.Builder<String> listBuilder = ImmutableList.builder();
        for (String[] parts : partsList) {
            listBuilder.add(parts);
        }
        return new PathExpressionImpl(listBuilder.build());
    }

    static PathExpression create(List<PathExpression> pathExpList) {
        ImmutableList.Builder<String> listBuilder = ImmutableList.builder();
        for (PathExpression pathExpression : pathExpList) {
            listBuilder.addAll(pathExpression.parts());
        }
        return new PathExpressionImpl(listBuilder.build());
    }

    PathExpression subPath(int fromIndex, int toIndex);

    boolean startsWith(String rootAlias);

    PathExpression concat(PathExpression... pathExpression);

    PathExpression concat(List<PathExpression> pathExpression);

    PathExpression concat(String... parts);

    PathExpression concat(List<String>... parts);

    static PathExpression parseAndCreate(String path) {
        return new PathExpressionImpl(path);
    }
}
