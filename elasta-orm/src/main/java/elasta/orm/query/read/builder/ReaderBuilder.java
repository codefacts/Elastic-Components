package elasta.orm.query.read.builder;

import elasta.orm.entity.core.Field;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.read.builder.ObjectReaderBuilderImpl.PathInfo;
import elasta.orm.query.read.builder.ex.ObjectReaderException;
import elasta.orm.query.read.impl.FieldAndIndexPair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static elasta.commons.Utils.not;

/**
 * Created by sohan on 5/1/2017.
 */
class ReaderBuilder {
    private final ObjectReaderBuilderImpl objectReaderBuilder;
    final Map<PathExpression, PathInfo> map = new HashMap<>();

    ReaderBuilder(ObjectReaderBuilderImpl objectReaderBuilder) {
        Objects.requireNonNull(objectReaderBuilder);
        this.objectReaderBuilder = objectReaderBuilder;
    }

    public Map<PathExpression, PathInfo> build() {

        for (int index = 0; index < objectReaderBuilder.fieldExpressions.size(); index++) {

            final PathExpression pathExpression = toFullPathExp(
                objectReaderBuilder.fieldExpressions.get(index).toPathExpression()
            );

            process(pathExpression, index);
        }

        return map;
    }

    private void process(PathExpression pathExpression, int index) {
        List<String> parts = pathExpression.parts();

        String entity = objectReaderBuilder.rootEntity;
        for (int i = 1, end = parts.size() - 1; i < end; i++) {
            String fieldName = parts.get(i);
            Field field = objectReaderBuilder.helper.getField(entity, fieldName);

            final PathInfo pathInfo = getOrCreatePathInfo(pathExpression.subPath(0, i));

            switch (field.getJavaType()) {
                case OBJECT: {
                    pathInfo.directRelationsBuilder.add(
                        pathExpression.subPath(0, i + 1)
                    );
                }
                break;
                case ARRAY: {
                    pathInfo.indirectRelationsBuilder.add(
                        pathExpression.subPath(0, i + 1)
                    );
                }
                break;
            }

            entity = field.getRelationship().get().getEntity();
        }

        Field field = objectReaderBuilder.helper.getField(entity, pathExpression.last());

        getOrCreatePathInfo(pathExpression.subPath(0, pathExpression.size() - 1))
            .fieldAndIndexPairsBuilder
            .add(
                new FieldAndIndexPair(field.getName(), index)
            )
        ;
    }

    private PathExpression toFullPathExp(PathExpression pathExpression) {
        if (not(pathExpression.startsWith(objectReaderBuilder.rootAlias))) {

            PathExpression fullPath = objectReaderBuilder.aliasToFullPathExpressionMap.get(pathExpression.root());

            if (fullPath == null) {
                throw new ObjectReaderException("Invalid path expression '" + pathExpression + "' not found in the aliasToFullPathExpressionMap");
            }

            return fullPath.concat(
                pathExpression.subPath(1, pathExpression.size())
            );
        }
        return pathExpression;
    }

    private PathInfo getOrCreatePathInfo(PathExpression pathExpression) {
        PathInfo pathInfo = map.get(pathExpression);
        if (pathInfo == null) {
            map.put(pathExpression, pathInfo = new PathInfo(objectReaderBuilder.helper, objectReaderBuilder.rootEntity));
        }
        return pathInfo;
    }
}
