package elasta.orm.nm.read.builder;

import com.google.common.collect.ImmutableList;
import elasta.orm.nm.entitymodel.EntityMappingHelper;
import elasta.orm.nm.entitymodel.Field;
import elasta.orm.nm.query.FieldExpression;
import elasta.orm.nm.query.PathExpression;
import elasta.orm.nm.read.ObjectReader;
import elasta.orm.nm.read.builder.ex.ObjectReaderException;
import elasta.orm.nm.read.impl.*;

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

        Map<PathIndex, PathInfo> map = new ReaderBuilder().build();

        PathInfo pathInfo = map.get(new PathIndex(PathExpression.create(rootAlias), 0));

        if (pathInfo == null) {
            throw new ObjectReaderException("No PathInfo found for pathInfo '" + pathInfo + "'");
        }

        return pathInfo.build(map);

    }

    final private class PathInfo {
        final ImmutableList.Builder<FieldAndIndexPair> fieldAndIndexPairsBuilder = ImmutableList.builder();
        final ImmutableList.Builder<PathIndex> directRelationsBuilder = ImmutableList.builder();
        final ImmutableList.Builder<PathIndex> indirectRelationsBuilder = ImmutableList.builder();

        public ObjectReader build(Map<PathIndex, PathInfo> map) {
            final ImmutableList<FieldAndIndexPair> fieldAndIndexPairs = fieldAndIndexPairsBuilder.build();
            final ImmutableList<PathIndex> directRelations = directRelationsBuilder.build();
            final ImmutableList<PathIndex> indirectRelations = indirectRelationsBuilder.build();

            ImmutableList.Builder<DirectRelation> directBuilder = ImmutableList.builder();
            directRelations.forEach(pathIndex -> {
                PathInfo pathInfo = map.get(pathIndex);

                if (pathInfo == null) {
                    throw new ObjectReaderException("No PathInfo found for pathInfo '" + pathIndex + "'");
                }

                directBuilder.add(
                    new DirectRelation(
                        field(pathIndex),
                        new DirectRelationReaderImpl(
                            pathInfo.build(map)
                        )
                    )
                );

            });

            ImmutableList.Builder<IndirectRelation> indirectBuilder = ImmutableList.builder();

            indirectRelations.forEach(pathIndex -> {
                PathInfo pathInfo = map.get(pathIndex);

                if (pathInfo == null) {
                    throw new ObjectReaderException("No PathInfo found for pathInfo '" + pathIndex + "'");
                }

                indirectBuilder.add(
                    new IndirectRelation(
                        field(pathIndex),
                        new IndirectRelationReaderImpl(
                            primaryKey(pathIndex),
                            pathInfo.build(map)
                        )
                    )
                );
            });

            return new ObjectReaderImpl(
                fieldAndIndexPairs,
                directBuilder.build(),
                indirectBuilder.build()
            );
        }

        private String field(PathIndex pathInfo) {
            boolean present = pathInfo.subPath.isPresent();
            if (present) {
                return pathInfo.subPath.get().last();
            }
            return pathInfo.pathExpression.last();
        }

        private String primaryKey(PathIndex pathIndex) {
            boolean present = pathIndex.subPath.isPresent();

            if (present) {
                String entity = rootEntity;
                String[] parts = pathIndex.pathExpression.parts();
                for (int i = 1; i < parts.length; i++) {
                    Field field = helper.getField(entity, parts[i]);
                    if (not(field.getRelationship().isPresent())) {
                        throw new ObjectReaderException("No child entity found for '" + entity + "." + parts[i] + "'");
                    }
                    entity = field.getRelationship().get().getEntity();
                }

                PathExpression pathExpression = pathIndex.subPath.get();
                parts = pathExpression.parts();
                for (int i = 0; i <= pathIndex.index; i++) {
                    Field field = helper.getField(entity, parts[i]);
                    if (not(field.getRelationship().isPresent())) {
                        throw new ObjectReaderException("No child entity found for '" + entity + "." + parts[i] + "'");
                    }
                    entity = field.getRelationship().get().getEntity();
                }

                return helper.getPrimaryKey(entity);
            }

            String entity = rootEntity;
            String[] parts = pathIndex.pathExpression.parts();
            for (int i = 1; i < parts.length; i++) {
                Field field = helper.getField(entity, parts[i]);
                if (not(field.getRelationship().isPresent())) {
                    throw new ObjectReaderException("No child entity found for '" + entity + "." + parts[i] + "'");
                }
                entity = field.getRelationship().get().getEntity();
            }

            return helper.getPrimaryKey(entity);
        }
    }

    static final private class PathIndex {
        final PathExpression pathExpression;
        final Optional<PathExpression> subPath;
        final int index;

        public PathIndex(PathExpression pathExpression, int index) {
            this(pathExpression, Optional.empty(), index);
        }

        private PathIndex(PathExpression pathExpression, Optional<PathExpression> subPath, int index) {
            this.pathExpression = pathExpression;
            this.subPath = subPath;
            this.index = index;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PathIndex pathIndex = (PathIndex) o;

            if (index != pathIndex.index) return false;

            if (!subPath.equals(pathIndex.subPath)) {
                return false;
            }
            String[] parts = pathExpression.parts();
            String[] parts1 = pathIndex.pathExpression.parts();
            for (int i = 0; i <= index; i++) {
                if (not(parts[i].equals(parts1[i]))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = pathExpression != null ? pathExpression.hashCode() : 0;
            result = 31 * result + (subPath != null ? subPath.hashCode() : 0);
            result = 31 * result + index;
            return result;
        }

        @Override
        public String toString() {
            return "PathIndex{" +
                "pathExpression=" + pathExpression +
                ", subPath=" + subPath +
                ", index=" + index +
                '}';
        }
    }

    private class ReaderBuilder {
        final Map<PathIndex, PathInfo> map = new HashMap<>();

        public Map<PathIndex, PathInfo> build() {

            for (int index = 0; index < fieldExpressions.size(); index++) {
                PathExpression pathExpression = fieldExpressions.get(index).toPathExpression();

                if (not(pathExpression.startsWith(rootAlias))) {

                    PathExpression fullPathToAlias = aliasToFullPathExpressionMap.get(pathExpression.root());
                    if (fullPathToAlias == null) {
                        throw new ObjectReaderException("Invalid path expression '" + pathExpression + "' not found in the aliasToFullPathExpressionMap");
                    }

                    String[] parts = fullPathToAlias.parts();
                    for (int i = 1, end = parts.length - 1; i < end; i++) {

                        final PathInfo pathInfo = getPathInfo(new PathIndex(fullPathToAlias, i - 1));

                        pathInfo.directRelationsBuilder.add(
                            new PathIndex(fullPathToAlias, i)
                        );
                    }

                    getPathInfo(new PathIndex(fullPathToAlias, parts.length - 2))
                        .indirectRelationsBuilder.add(new PathIndex(fullPathToAlias, parts.length - 1));

                    parts = pathExpression.parts();

                    for (int i = 1; i < parts.length; i++) {
                        PathInfo pathInfo = getPathInfo(new PathIndex(
                            fullPathToAlias,
                            Optional.of(pathExpression),
                            i - 1
                        ));

                        if ((parts.length - 1) == i) {

                            pathInfo.fieldAndIndexPairsBuilder.add(
                                new FieldAndIndexPair(parts[i], index)
                            );

                            continue;
                        }

                        pathInfo.directRelationsBuilder.add(
                            new PathIndex(
                                fullPathToAlias,
                                Optional.of(pathExpression),
                                i
                            )
                        );

                    }

                    continue;
                }

                directRelation(pathExpression, index);
            }

            return map;
        }

        private void directRelation(PathExpression pathExpression, int index) {
            String[] parts = pathExpression.parts();
            for (int i = 1; i < parts.length; i++) {

                final PathInfo pathInfo = getPathInfo(new PathIndex(pathExpression, i - 1));

                if ((parts.length - 1) == i) {

                    pathInfo.fieldAndIndexPairsBuilder.add(
                        new FieldAndIndexPair(parts[i], index)
                    );

                    continue;
                }

                pathInfo.directRelationsBuilder.add(
                    new PathIndex(pathExpression, i)
                );
            }
        }

        private PathInfo getPathInfo(PathIndex pathIndex) {
            PathInfo pathInfo = map.get(pathIndex);
            if (pathInfo == null) {
                map.put(pathIndex, pathInfo = new PathInfo());
            }
            return pathInfo;
        }
    }
}
