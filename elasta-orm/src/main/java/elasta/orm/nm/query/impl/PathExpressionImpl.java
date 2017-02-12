package elasta.orm.nm.query.impl;

import elasta.orm.nm.query.PathExpression;
import elasta.orm.nm.query.ex.PathExpressionException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Jango on 17/02/10.
 */
final public class PathExpressionImpl implements PathExpression {
    final String[] parts;

    public PathExpressionImpl(String pathExpression) {
        Objects.requireNonNull(pathExpression);
        List<String> list = Stream.of(pathExpression.split("\\."))
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());

        if (list.size() < 1) {
            throw new PathExpressionException("No elements found in path Expression '" + pathExpression + "'");
        }

        this.parts = list.toArray(new String[list.size()]);
    }

    public PathExpressionImpl(String[] parts) {
        Objects.requireNonNull(parts);
        this.parts = parts;
    }

    @Override
    public Optional<PathExpression> getParent() {

        if (parts.length < 2) {
            return Optional.empty();
        }

        return Optional.of(new PathExpressionImpl(Arrays.copyOf(parts, parts.length - 1)));
    }

    @Override
    public String[] parts() {
        return parts;
    }

    @Override
    public String getAt(int index) {
        return parts[index];
    }

    @Override
    public int size() {
        return parts.length;
    }

    @Override
    public String root() {
        return parts[0];
    }

    @Override
    public String last() {
        return parts[parts.length - 1];
    }

    @Override
    public PathExpression subPath(int fromIndex, int toIndex) {
        return new PathExpressionImpl(Arrays.copyOfRange(parts, fromIndex, toIndex));
    }
}
