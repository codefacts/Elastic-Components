package elasta.orm.query.expression.impl;

import com.google.common.collect.ImmutableMap;
import elasta.criteria.Func;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.ColumnType;
import elasta.orm.entity.core.Field;
import elasta.orm.entity.core.columnmapping.DbColumnMapping;
import elasta.orm.entity.core.columnmapping.SimpleDbColumnMapping;
import elasta.orm.query.ex.QueryParserException;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.expression.builder.FieldExpressionAndOrderPair;
import elasta.orm.query.expression.builder.FieldExpressionResolverImpl;
import elasta.orm.query.expression.builder.impl.PathExpressionAndAliasPair;
import elasta.orm.query.expression.core.AliasAndColumn;
import elasta.orm.query.expression.core.JoinTpl;
import elasta.sql.SqlExecutor;
import elasta.sql.core.JoinType;

import java.util.*;

import static elasta.commons.Utils.not;

/**
 * Created by sohan on 4/9/2017.
 */
final class FieldExpressionToAliasAndColumnMapTranslator {
    final String rootEntity;
    final String rootAlias;
    final FieldExpressionResolverImpl selectFieldExpressionResolver;
    final FieldExpressionResolverImpl expressionResolver;
    final List<PathExpressionAndAliasPair> fromPathExpressionAndAliasPairs;
    final EntityMappingHelper helper;
    final Map<String, Map<String, QueryImpl.PartAndJoinTpl>> partAndJoinTplMap;
    final String ALIAS_STR = "a";
    final QueryImpl.AliasCounter aliasCounter;

    public FieldExpressionToAliasAndColumnMapTranslator(String rootEntity, String rootAlias, FieldExpressionResolverImpl selectFieldExpressionResolver, FieldExpressionResolverImpl expressionResolver, List<PathExpressionAndAliasPair> fromPathExpressionAndAliasPairs, EntityMappingHelper helper, Map<String, Map<String, QueryImpl.PartAndJoinTpl>> partAndJoinTplMap, QueryImpl.AliasCounter aliasCounter) {
        this.rootEntity = rootEntity;
        this.rootAlias = rootAlias;
        this.selectFieldExpressionResolver = selectFieldExpressionResolver;
        this.expressionResolver = expressionResolver;
        this.fromPathExpressionAndAliasPairs = fromPathExpressionAndAliasPairs;
        this.helper = helper;
        this.partAndJoinTplMap = partAndJoinTplMap;
        this.aliasCounter = aliasCounter;
    }

    public ImmutableMap<FieldExpression, AliasAndColumn> translate(final Map<String, PathExpression> aliasToFullPathExpressionMap) {

        final ImmutableMap.Builder<String, String> aliasToEntityMapBuilder = ImmutableMap.builder();

        aliasToEntityMapBuilder.put(rootAlias, rootEntity);

        Map<String, Optional<JoinType>> aliasToJoinTypeMap = toAliasToJoinTypeMap(fromPathExpressionAndAliasPairs);

        final Map<String, QueryImpl.PartAndJoinTpl> rootMap = new LinkedHashMap<>();
        partAndJoinTplMap.put(rootAlias, rootMap);

        aliasToFullPathExpressionMap.forEach((alias, pathExpression) -> {

            final List<String> parts = pathExpression.parts();
            Map<String, QueryImpl.PartAndJoinTpl> tplMap = rootMap;
            String entity = rootEntity;
            String entityAlias = rootAlias;
            for (int i = 1, end = parts.size(); i < end; i++) {
                String childEntityField = parts.get(i);

                QueryImpl.PartAndJoinTpl partAndJoinTpl = tplMap.get(childEntityField);

                if (partAndJoinTpl == null) {

                    String childEntity = getChildEntity(entity, childEntityField);

                    final boolean isLast = (i == parts.size() - 1);

                    final String childAlias = isLast ? alias : createAlias();

                    tplMap.put(
                        childEntityField,
                        partAndJoinTpl = new QueryImpl.PartAndJoinTpl(
                            new JoinTpl(
                                entityAlias,
                                childAlias,
                                entity,
                                childEntityField,
                                childEntity,
                                isLast ? aliasToJoinTypeMap.getOrDefault(alias, Optional.empty()) : Optional.empty()
                            )
                        )
                    );
                }

                entity = partAndJoinTpl.joinTpl.getChildEntity();
                entityAlias = partAndJoinTpl.joinTpl.getChildEntityAlias();
                tplMap = partAndJoinTpl.partAndJoinTplMap;
            }

            aliasToEntityMapBuilder.put(entityAlias, entity);
            partAndJoinTplMap.put(entityAlias, new LinkedHashMap<>());
        });

        final ImmutableMap<String, String> aliasToEntityMap = aliasToEntityMapBuilder.build();

        final ImmutableMap.Builder<FieldExpression, AliasAndColumn> fieldExpToAliasedColumnMapBuilder = ImmutableMap.builder();

        selectFieldExpressionResolver.getFuncMap().keySet()
            .forEach(fieldExpression -> applyFieldExp(fieldExpression, aliasToEntityMap, fieldExpToAliasedColumnMapBuilder));

        expressionResolver.getFuncMap().keySet()
            .forEach(fieldExpression -> applyFieldExp(fieldExpression, aliasToEntityMap, fieldExpToAliasedColumnMapBuilder));

        return fieldExpToAliasedColumnMapBuilder.build();
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
        final PathExpression pathExpression = fieldExpression.getParentPath();
        final String alias = pathExpression.root();

        if (not(aliasToEntityMap.containsKey(alias))) {
            throw new QueryParserException("Field Expression '" + fieldExpression + "' starts with an invalid alias");
        }

        final List<String> parts = pathExpression.parts();

        Map<String, QueryImpl.PartAndJoinTpl> tplMap = partAndJoinTplMap.get(alias);
        String entity = aliasToEntityMap.get(alias);
        String entityAlias = alias;

        for (int i = 1, end = parts.size(); i < end; i++) {
            String childEntityField = parts.get(i);

            QueryImpl.PartAndJoinTpl partAndJoinTpl = tplMap.get(childEntityField);

            if (partAndJoinTpl == null) {

                String childEntity = getChildEntity(entity, childEntityField);

                final String childAlias = createAlias();

                tplMap.put(
                    childEntityField,
                    partAndJoinTpl = new QueryImpl.PartAndJoinTpl(
                        new JoinTpl(entityAlias, childAlias, entity, childEntityField, childEntity, Optional.empty())
                    )
                );
            }

            entity = partAndJoinTpl.joinTpl.getChildEntity();
            entityAlias = partAndJoinTpl.joinTpl.getChildEntityAlias();
            tplMap = partAndJoinTpl.partAndJoinTplMap;
        }

        fieldExpToAliasedColumnMapBuilder.put(
            fieldExpression,
            new AliasAndColumn(
                entityAlias,
                getColumn(
                    entity,
                    fieldExpression.getField()
                )
            )
        );
    }

    private String getColumn(String entity, String field) {

        final DbColumnMapping columnMapping = helper.getColumnMapping(entity, field);

        if (columnMapping.getColumnType() != ColumnType.SIMPLE) {
            throw new QueryParserException(entity + "." + field + " does not map to simple column type");
        }

        return ((SimpleDbColumnMapping) columnMapping).getColumn();
    }

    private String getChildEntity(String entity, String childEntityField) {

        Field field = helper.getField(entity, childEntityField);

        return field.getRelationship().orElseThrow(() -> new QueryParserException("No relationship found in " + entity + "." + childEntityField)).getEntity();
    }

    private String createAlias() {
        return ALIAS_STR + String.valueOf(aliasCounter.aliasCount++);
    }
}
