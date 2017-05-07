package elasta.orm.query.expression.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.orm.query.ex.PathExpressionException;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.expression.builder.impl.PathExpressionAndAliasPair;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static elasta.commons.Utils.not;

/**
 * Created by sohan on 4/9/2017.
 */
final public class AliasToFullPathExpressionBuilder {
    final String rootAlias;
    final String rootEntity;
    final Stream<PathExpressionAndAliasPair> fromPathExpressionAndAliasPairs;
    final Map<String, PathExpression> map;

    public AliasToFullPathExpressionBuilder(String rootAlias, String rootEntity, Stream<PathExpressionAndAliasPair> fromPathExpressionAndAliasPairs) {
        this.rootAlias = rootAlias;
        this.rootEntity = rootEntity;
        Stream.Builder<PathExpressionAndAliasPair> streamBuilder = Stream.builder();
        this.map = toMap(fromPathExpressionAndAliasPairs, streamBuilder);
        this.fromPathExpressionAndAliasPairs = streamBuilder.build();
    }

    private Map<String, PathExpression> toMap(Stream<PathExpressionAndAliasPair> expressionAndAliasPairs, Stream.Builder<PathExpressionAndAliasPair> streamBuilder) {
        return expressionAndAliasPairs
            .peek(streamBuilder::accept)
            .collect(Collectors.toMap(
                PathExpressionAndAliasPair::getAlias,
                PathExpressionAndAliasPair::getPathExpression
            ));
    }


    public Map<String, PathExpression> build() {

        final ImmutableMap.Builder<String, PathExpression> aliasToPathExpressionMapBuilder = ImmutableMap.builder();

        fromPathExpressionAndAliasPairs.forEach(pathExpressionAndAliasPair -> {

            final PathExpression fullPathExpression = createFullPathExpression(pathExpressionAndAliasPair.getPathExpression());

            aliasToPathExpressionMapBuilder.put(pathExpressionAndAliasPair.getAlias(), fullPathExpression);
        });

        return aliasToPathExpressionMapBuilder.build();
    }

    private PathExpression createFullPathExpression(PathExpression pathExpression) {

        final ImmutableList.Builder<PathExpression> pathExpListBuilder = ImmutableList.builder();

        pathExpListBuilder.add(pathExpression);

        for (; ; ) {

            if (Utils.not(
                map.containsKey(pathExpression.root())
            )) {

                if (not(pathExpression.root().equals(rootAlias))) {
                    throw new PathExpressionException("Path '" + pathExpression + "' must start with root alias '" + rootAlias + "'");
                }
                break;
            }

            pathExpression = map.get(pathExpression.root());

            pathExpListBuilder.add(pathExpression);
        }

        return fullPathExpression(pathExpListBuilder.build());
    }

    private PathExpression fullPathExpression(ImmutableList<PathExpression> pathExpressions) {

        return PathExpression.create(rootAlias).concat(
            pathExpressions
                .stream().map(pathExpression -> pathExpression.subPath(1, pathExpression.size()))
                .collect(Collectors.toList())
        );
    }
}
