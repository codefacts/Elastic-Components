package elasta.orm.query.expression.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.ex.PathExpressionException;

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
    final List<String> parts;

    public PathExpressionImpl(String pathExpression) {
        Objects.requireNonNull(pathExpression);
        List<String> list = Stream.of(pathExpression.split("\\."))
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());

        if (list.size() < 1) {
            throw new PathExpressionException("No elements found in path Expression '" + pathExpression + "'");
        }

        this.parts = ImmutableList.copyOf(list);
    }

    public PathExpressionImpl(List<String> parts) {
        Objects.requireNonNull(parts);
        this.parts = ImmutableList.copyOf(parts);
    }

    @Override
    public Optional<PathExpression> getParent() {

        if (parts.size() < 2) {
            return Optional.empty();
        }

        return Optional.of(
            new PathExpressionImpl(parts.subList(0, parts.size() - 1))
        );
    }

    @Override
    public List<String> parts() {
        return parts;
    }

    @Override
    public String getAt(int index) {
        return parts.get(index);
    }

    @Override
    public int size() {
        return parts.size();
    }

    @Override
    public String root() {
        return parts.get(0);
    }

    @Override
    public String last() {
        return parts.get(parts.size() - 1);
    }

    @Override
    public PathExpression subPath(int fromIndex, int toIndex) {
        return new PathExpressionImpl(parts.subList(fromIndex, toIndex));
    }

    @Override
    public boolean startsWith(String rootAlias) {
        return root().equals(rootAlias);
    }

    @Override
    public PathExpression concat(PathExpression... pathExpression) {

        ImmutableList.Builder<String> partsBuilder = ImmutableList.builder();

        partsBuilder.addAll(this.parts);

        for (PathExpression expression : pathExpression) {
            partsBuilder.addAll(expression.parts());
        }

        return new PathExpressionImpl(
            partsBuilder.build()
        );
    }

    @Override
    public PathExpression concat(List<PathExpression> pathExpressionList) {
        ImmutableList.Builder<String> listBuilder = ImmutableList.builder();
        listBuilder.addAll(this.parts);
        for (PathExpression pathExpression : pathExpressionList) {
            listBuilder.addAll(pathExpression.parts());
        }
        return new PathExpressionImpl(
            listBuilder.build()
        );
    }

    @Override
    public PathExpression concat(String... parts) {

        return new PathExpressionImpl(
            ImmutableList.<String>builder().addAll(this.parts).add(parts).build()
        );
    }

    @Override
    public PathExpression concat(List<String>[] partsList) {

        ImmutableList.Builder<String> listBuilder = ImmutableList.builder();

        listBuilder.addAll(this.parts);

        for (List<String> parts : partsList) {
            listBuilder.addAll(parts);
        }

        return new PathExpressionImpl(
            listBuilder.build()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PathExpressionImpl that = (PathExpressionImpl) o;

        return parts.equals(that.parts);
    }

    @Override
    public int hashCode() {
        return parts.hashCode();
    }

    @Override
    public String toString() {
        return parts.stream().collect(Collectors.joining("."));
    }
}
