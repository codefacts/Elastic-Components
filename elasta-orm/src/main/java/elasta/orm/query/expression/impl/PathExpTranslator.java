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

import static elasta.commons.Utils.not;

/**
 * Created by sohan on 4/9/2017.
 */
class PathExpTranslator {
    final String rootAlias;
    final String rootEntity;
    final List<PathExpressionAndAliasPair> fromPathExpressionAndAliasPairs;
    final Map<String, PathExpression> map;

    public PathExpTranslator(String rootAlias, String rootEntity, List<PathExpressionAndAliasPair> fromPathExpressionAndAliasPairs) {
        this.rootAlias = rootAlias;
        this.rootEntity = rootEntity;
        this.fromPathExpressionAndAliasPairs = fromPathExpressionAndAliasPairs;

        this.map = fromPathExpressionAndAliasPairs.stream().collect(Collectors.toMap(
            PathExpressionAndAliasPair::getAlias,
            PathExpressionAndAliasPair::getPathExpression
        ));
    }


    public Map<String, PathExpression> getAliasToFullPathExpressionMap() {

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
