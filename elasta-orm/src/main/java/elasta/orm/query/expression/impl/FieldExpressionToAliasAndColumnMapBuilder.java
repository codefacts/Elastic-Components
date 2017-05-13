package elasta.orm.query.expression.impl;

import com.google.common.collect.ImmutableMap;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.Field;
import elasta.orm.entity.core.columnmapping.ColumnMapping;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.ex.QueryParserException;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.expression.builder.FieldExpressionResolverImpl;
import elasta.orm.query.expression.builder.impl.PathExpressionAndAliasPair;
import elasta.orm.query.expression.core.AliasAndColumn;
import elasta.orm.query.expression.core.JoinTpl;
import elasta.sql.core.JoinType;
import lombok.Builder;
import lombok.Value;

import java.util.*;

import static elasta.commons.Utils.not;

/**
 * Created by sohan on 4/9/2017.
 */
final class FieldExpressionToAliasAndColumnMapBuilder {
    final String rootEntity;
    final String rootAlias;
    final FieldExpressionResolverImpl selectFieldExpressionResolver;
    final FieldExpressionResolverImpl expressionResolver;
    final Collection<FieldExpression> groupBy;
    final List<PathExpressionAndAliasPair> fromPathExpressionAndAliasPairs;
    final EntityMappingHelper helper;
    final AliasGenerator aliasGenerator;

    final Map<String, Map<String, QueryImpl.PartAndJoinTpl>> aliasToJoinTplMap = new LinkedHashMap<>();

    FieldExpressionToAliasAndColumnMapBuilder(String rootEntity, String rootAlias, FieldExpressionResolverImpl selectFieldExpressionResolver, FieldExpressionResolverImpl expressionResolver, Collection<FieldExpression> groupBy, List<PathExpressionAndAliasPair> fromPathExpressionAndAliasPairs, EntityMappingHelper helper, AliasGenerator aliasGenerator) {
        this.rootEntity = rootEntity;
        this.rootAlias = rootAlias;
        this.selectFieldExpressionResolver = selectFieldExpressionResolver;
        this.expressionResolver = expressionResolver;
        this.groupBy = groupBy;
        this.fromPathExpressionAndAliasPairs = fromPathExpressionAndAliasPairs;
        this.helper = helper;
        this.aliasGenerator = aliasGenerator;
    }

    Tpl3 build(final Map<String, PathExpression> aliasToFullPathExpressionMap) {

        final ImmutableMap.Builder<String, String> aliasToEntityMapBuilder = ImmutableMap.builder();

        aliasToEntityMapBuilder.put(rootAlias, rootEntity);

        Map<String, Optional<JoinType>> aliasToJoinTypeMap = toAliasToJoinTypeMap(fromPathExpressionAndAliasPairs);

        final Map<String, QueryImpl.PartAndJoinTpl> rootMap = new LinkedHashMap<>();
        aliasToJoinTplMap.put(rootAlias, rootMap);

        aliasToFullPathExpressionMap.forEach((alias, fullPathExpression) -> {

            EntityAndAliasPair entityAndAliasPair = populatePartAndJoinTplMap(
                PartAndJoinTplParams.builder()
                    .pathExpression(fullPathExpression)
                    .entity(rootEntity)
                    .entityAlias(rootAlias)
                    .tplMap(rootMap)
                    .aliasProvider(isLast -> isLast ? alias : createAlias())
                    .joinTypeProvider(isLast -> isLast ? aliasToJoinTypeMap.getOrDefault(alias, Optional.empty()) : Optional.empty())
                    .build()
            );

            aliasToEntityMapBuilder.put(entityAndAliasPair.entityAlias, entityAndAliasPair.entity);
            aliasToJoinTplMap.put(entityAndAliasPair.entityAlias, new LinkedHashMap<>());
        });

        final ImmutableMap<String, String> aliasToEntityMap = aliasToEntityMapBuilder.build();

        final ImmutableMap.Builder<FieldExpression, AliasAndColumn> selectFieldExpToAliasedColumnMapBuilder = ImmutableMap.builder();

        selectFieldExpressionResolver.getFuncMap().keySet()
            .forEach(fieldExpression -> applyFieldExp(fieldExpression, aliasToEntityMap, selectFieldExpToAliasedColumnMapBuilder));

        final ImmutableMap.Builder<FieldExpression, AliasAndColumn> fieldExpToAliasedColumnMapBuilder = ImmutableMap.builder();

        expressionResolver.getFuncMap().keySet()
            .forEach(fieldExpression -> applyFieldExp(fieldExpression, aliasToEntityMap, fieldExpToAliasedColumnMapBuilder));

        groupBy.forEach(fieldExpression -> applyFieldExp(fieldExpression, aliasToEntityMap, fieldExpToAliasedColumnMapBuilder));

        return new Tpl3(selectFieldExpToAliasedColumnMapBuilder.build(), fieldExpToAliasedColumnMapBuilder.build(), aliasToJoinTplMap);
    }

    private EntityAndAliasPair populatePartAndJoinTplMap(PartAndJoinTplParams params) {

        final PathExpression pathExpression = params.getPathExpression();
        final AliasProvider aliasProvider = params.getAliasProvider();
        final JoinTypeProvider joinTypeProvider = params.getJoinTypeProvider();

        String entity = params.getEntity();
        String entityAlias = params.getEntityAlias();
        Map<String, QueryImpl.PartAndJoinTpl> tplMap = params.getTplMap();

        final List<String> parts = pathExpression.parts();
        for (int i = 1, end = parts.size(); i < end; i++) {
            String childEntityField = parts.get(i);

            QueryImpl.PartAndJoinTpl partAndJoinTpl = tplMap.get(childEntityField);

            if (partAndJoinTpl == null) {

                String childEntity = getChildEntity(entity, childEntityField);

                final boolean isLast = (i == parts.size() - 1);

                final String childEntityAlias = aliasProvider.provide(isLast);

                tplMap.put(
                    childEntityField,
                    partAndJoinTpl = new QueryImpl.PartAndJoinTpl(
                        new JoinTpl(
                            entityAlias,
                            childEntityAlias,
                            entity,
                            childEntityField,
                            childEntity,
                            joinTypeProvider.provide(isLast)
                        )
                    )
                );
            }

            entity = partAndJoinTpl.joinTpl.getChildEntity();
            entityAlias = partAndJoinTpl.joinTpl.getChildEntityAlias();
            tplMap = partAndJoinTpl.partToJoinTplMap;
        }

        return new EntityAndAliasPair(entity, entityAlias);
    }

    private Map<String, Optional<JoinType>> toAliasToJoinTypeMap(List<PathExpressionAndAliasPair> fromPathExpressionAndAliasPairs) {
        final ImmutableMap.Builder<String, Optional<JoinType>> mapBuilder = ImmutableMap.builder();

        fromPathExpressionAndAliasPairs.forEach(pair -> {
            mapBuilder.put(pair.getAlias(), pair.getJoinType());
        });

        return mapBuilder.build();
    }

    private void applyFieldExp(
        FieldExpression fieldExpression,
        ImmutableMap<String, String> aliasToEntityMap,
        ImmutableMap.Builder<FieldExpression, AliasAndColumn> fieldExpToAliasedColumnMapBuilder
    ) {
        final PathExpression pathExpression = fieldExpression.getParent();
        final String alias = pathExpression.root();

        if (not(aliasToEntityMap.containsKey(alias))) {
            throw new QueryParserException("Field Expression '" + fieldExpression + "' starts with an invalid alias");
        }

        EntityAndAliasPair entityAndAliasPair = populatePartAndJoinTplMap(
            PartAndJoinTplParams.builder()
                .pathExpression(pathExpression)
                .entity(aliasToEntityMap.get(alias))
                .entityAlias(alias)
                .tplMap(aliasToJoinTplMap.get(alias))
                .aliasProvider(isLast -> createAlias())
                .joinTypeProvider(isLast -> Optional.empty())
                .build()
        );

        fieldExpToAliasedColumnMapBuilder.put(
            fieldExpression,
            new AliasAndColumn(
                entityAndAliasPair.entityAlias,
                getColumn(
                    entityAndAliasPair.entity,
                    fieldExpression.getField()
                )
            )
        );
    }

    private String getColumn(String entity, String field) {

        final ColumnMapping columnMapping = helper.getColumnMapping(entity, field);

        return (columnMapping).getColumn();
    }

    private String getChildEntity(String entity, String childEntityField) {

        Field field = helper.getField(entity, childEntityField);

        return field.getRelationship().orElseThrow(() -> new QueryParserException("No relationship found in " + entity + "." + childEntityField)).getEntity();
    }

    private String createAlias() {
        return aliasGenerator.generate();
    }

    private interface AliasProvider {

        String provide(boolean isLast);
    }

    private interface JoinTypeProvider {
        Optional<JoinType> provide(boolean isLast);
    }

    private final class EntityAndAliasPair {
        final String entity;
        final String entityAlias;

        EntityAndAliasPair(String entity, String entityAlias) {
            this.entity = entity;
            this.entityAlias = entityAlias;
        }
    }

    @Value
    @Builder
    final private static class PartAndJoinTplParams {
        PathExpression pathExpression;
        String entity;
        String entityAlias;
        Map<String, QueryImpl.PartAndJoinTpl> tplMap;
        AliasProvider aliasProvider;
        JoinTypeProvider joinTypeProvider;
    }
}
