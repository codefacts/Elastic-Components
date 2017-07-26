package elasta.orm.query.read.builder.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.Field;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.read.ObjectReader;
import elasta.orm.query.read.builder.ObjectReaderBuilder;
import elasta.orm.query.read.builder.ex.InvalidPrimaryKeyIndexException;
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

        Map<PathExpression, PathInfo> map = new ReaderBuilder(this).build();

        PathInfo pathInfo = map.get(PathExpression.create(rootAlias));

        if (pathInfo == null) {
            throw new ObjectReaderException("No PathInfo found for pathInfo '" + pathInfo + "'");
        }

        return pathInfo.build(map);

    }

    static final class PathInfo {
        final EntityMappingHelper helper;
        final String rootEntity;
        final ImmutableSet.Builder<FieldAndIndexPair> fieldAndIndexPairsBuilder = ImmutableSet.builder();
        final ImmutableSet.Builder<PathExpression> directRelationsBuilder = ImmutableSet.builder();
        final ImmutableSet.Builder<PathExpression> indirectRelationsBuilder = ImmutableSet.builder();
        private int primaryKeyIndex = -1;

        public PathInfo(EntityMappingHelper helper, String rootEntity) {
            this.helper = helper;
            this.rootEntity = rootEntity;
        }

        public ObjectReader build(Map<PathExpression, PathInfo> map) {

            if (primaryKeyIndex < 0) {
                throw new InvalidPrimaryKeyIndexException("Invalid primary key index " + primaryKeyIndex + "");
            }

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
                            pathInfo.build(map),
                            primaryKeyIndex
                        )
                    )
                );
            });

            return new ObjectReaderImpl(
                ImmutableList.copyOf(fieldAndIndexPairs),
                ImmutableList.copyOf(directBuilder.build()),
                ImmutableList.copyOf(indirectBuilder.build()),
                primaryKeyIndex
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

        public PathInfo setPrimaryKeyIndex(int primaryKeyIndex) {
            this.primaryKeyIndex = primaryKeyIndex;
            return this;
        }
    }

}
