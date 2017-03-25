package elasta.orm.query.read.builder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.Field;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.read.ObjectReader;
import elasta.orm.query.read.builder.ex.ObjectReaderException;
import elasta.orm.query.read.impl.*;

import java.util.*;

import static elasta.commons.Utils.not;

/**
 * Created by Jango on 17/02/12.
 */
final public class ObjectReaderBuilderImpl implements ObjectReaderBuilder {
    final String rootAlias;
    final String rootEntity;
    final List<FieldExpression> fieldExpressions;
    final Map<String, PathExpression> aliasToFullPathExpressionMap;
    final EntityMappingHelper helper;

    public ObjectReaderBuilderImpl(String rootAlias, String rootEntity, List<FieldExpression> fieldExpressions, Map<String, PathExpression> aliasToFullPathExpressionMap, EntityMappingHelper helper) {
        Objects.requireNonNull(rootAlias);
        Objects.requireNonNull(rootEntity);
        Objects.requireNonNull(fieldExpressions);
        Objects.requireNonNull(aliasToFullPathExpressionMap);
        Objects.requireNonNull(helper);
        this.rootAlias = rootAlias;
        this.rootEntity = rootEntity;
        this.fieldExpressions = fieldExpressions;
        this.aliasToFullPathExpressionMap = aliasToFullPathExpressionMap;
        this.helper = helper;
    }

    @Override
    public ObjectReader build() {

        Map<PathExpression, PathInfo> map = new ReaderBuilder().build();

        PathInfo pathInfo = map.get(PathExpression.create(rootAlias));

        if (pathInfo == null) {
            throw new ObjectReaderException("No PathInfo found for pathInfo '" + pathInfo + "'");
        }

        return pathInfo.build(map);

    }

    final private class PathInfo {
        final ImmutableSet.Builder<FieldAndIndexPair> fieldAndIndexPairsBuilder = ImmutableSet.builder();
        final ImmutableSet.Builder<PathExpression> directRelationsBuilder = ImmutableSet.builder();
        final ImmutableSet.Builder<PathExpression> indirectRelationsBuilder = ImmutableSet.builder();

        public ObjectReader build(Map<PathExpression, PathInfo> map) {
            final ImmutableSet<FieldAndIndexPair> fieldAndIndexPairs = fieldAndIndexPairsBuilder.build();
            final ImmutableSet<PathExpression> directRelations = directRelationsBuilder.build();
            final ImmutableSet<PathExpression> indirectRelations = indirectRelationsBuilder.build();

            ImmutableList.Builder<DirectRelation> directBuilder = ImmutableList.builder();
            directRelations.forEach(pathExpression -> {
                PathInfo pathInfo = map.get(pathExpression);

                if (pathInfo == null) {
                    throw new ObjectReaderException("No PathInfo found for pathInfo '" + pathExpression + "'");
                }

                directBuilder.add(
                    new DirectRelation(
                        field(pathExpression),
                        new DirectRelationReaderImpl(
                            pathInfo.build(map)
                        )
                    )
                );

            });

            ImmutableList.Builder<IndirectRelation> indirectBuilder = ImmutableList.builder();

            indirectRelations.forEach(pathExpression -> {
                PathInfo pathInfo = map.get(pathExpression);

                if (pathInfo == null) {
                    throw new ObjectReaderException("No PathInfo found for pathInfo '" + pathExpression + "'");
                }

                indirectBuilder.add(
                    new IndirectRelation(
                        field(pathExpression),
                        new IndirectRelationReaderImpl(
                            primaryKey(pathExpression),
                            pathInfo.build(map)
                        )
                    )
                );
            });

            return new ObjectReaderImpl(
                ImmutableList.copyOf(fieldAndIndexPairs),
                ImmutableList.copyOf(directBuilder.build()),
                ImmutableList.copyOf(indirectBuilder.build())
            );
        }

        private String field(PathExpression pathExpression) {
            return pathExpression.last();
        }

        private String primaryKey(PathExpression pathExpression) {

            String entity = rootEntity;
            List<String> parts = pathExpression.parts();
            for (int i = 1; i < parts.size(); i++) {
                Field field = helper.getField(entity, parts.get(i));
                if (not(field.getRelationship().isPresent())) {
                    throw new ObjectReaderException("No child entity found for '" + entity + "." + parts.get(i) + "'");
                }
                entity = field.getRelationship().get().getEntity();
            }

            return helper.getPrimaryKey(entity);
        }
    }

    private class ReaderBuilder {
        final Map<PathExpression, PathInfo> map = new HashMap<>();

        public Map<PathExpression, PathInfo> build() {

            for (int index = 0; index < fieldExpressions.size(); index++) {
                final PathExpression pathExpression = toFullPathExp(
                    fieldExpressions.get(index).toPathExpression()
                );

                process(pathExpression, index);
            }

            return map;
        }

        private void process(PathExpression pathExpression, int index) {
            List<String> parts = pathExpression.parts();

            String entity = rootEntity;
            for (int i = 1, end = parts.size() - 1; i < end; i++) {
                String fieldName = parts.get(i);
                Field field = helper.getField(entity, fieldName);

                final PathInfo pathInfo = getPathInfo(pathExpression.subPath(0, i));

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

            Field field = helper.getField(entity, pathExpression.last());

            getPathInfo(pathExpression.subPath(0, pathExpression.size() - 1))
                .fieldAndIndexPairsBuilder
                .add(
                    new FieldAndIndexPair(field.getName(), index)
                )
            ;
        }

        private PathExpression toFullPathExp(PathExpression pathExpression) {
            if (not(pathExpression.startsWith(rootAlias))) {

                PathExpression fullPath = aliasToFullPathExpressionMap.get(pathExpression.root());

                if (fullPath == null) {
                    throw new ObjectReaderException("Invalid path expression '" + pathExpression + "' not found in the aliasToFullPathExpressionMap");
                }

                return fullPath.concat(
                    pathExpression.subPath(1, pathExpression.size())
                );
            }
            return pathExpression;
        }

        private PathInfo getPathInfo(PathExpression pathExpression) {
            PathInfo pathInfo = map.get(pathExpression);
            if (pathInfo == null) {
                map.put(pathExpression, pathInfo = new PathInfo());
            }
            return pathInfo;
        }
    }
}
