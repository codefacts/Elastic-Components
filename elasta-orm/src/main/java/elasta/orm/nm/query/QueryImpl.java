package elasta.orm.nm.query;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.orm.json.sql.core.JoinType;
import elasta.orm.nm.criteria.Func;
import elasta.orm.nm.criteria.funcs.ParamsBuilderImpl;
import elasta.orm.nm.entitymodel.ColumnType;
import elasta.orm.nm.entitymodel.EntityMappingHelper;
import elasta.orm.nm.entitymodel.Field;
import elasta.orm.nm.entitymodel.Relationship;
import elasta.orm.nm.entitymodel.columnmapping.*;
import elasta.orm.nm.query.builder.FieldExpressionAndOrderPair;
import elasta.orm.nm.query.builder.FieldExpressionHolderFunc;
import elasta.orm.nm.query.builder.FieldExpressionResolverImpl;
import elasta.orm.nm.query.builder.impl.PathExpressionAndAliasPair;
import elasta.orm.nm.query.ex.PathExpressionException;
import elasta.orm.nm.query.ex.QueryParserException;
import elasta.orm.nm.query.impl.*;
import elasta.orm.nm.upsert.ColumnToColumnMapping;

import java.util.*;
import java.util.stream.Collectors;

import static elasta.commons.Utils.not;

/**
 * Created by Jango on 17/02/10.
 */
final public class QueryImpl implements Query {
    final String sql;

    public QueryImpl(String rootEntity, String rootAlias, FieldExpressionResolverImpl selectFieldExpressionResolver, FieldExpressionResolverImpl expressionResolver, List<Func> selectFuncs, List<PathExpressionAndAliasPair> fromPathExpressionAndAliasPairs, List<Func> whereFuncs, List<FieldExpressionAndOrderPair> orderByPairs, List<FieldExpression> groupByExpressions, List<Func> havingFuncs, EntityMappingHelper entityMappingHelper) {
        this.sql = new QQ(
            rootEntity,
            rootAlias,
            selectFieldExpressionResolver,
            expressionResolver,
            selectFuncs,
            fromPathExpressionAndAliasPairs,
            whereFuncs,
            orderByPairs,
            groupByExpressions,
            havingFuncs,
            entityMappingHelper).toSql();
    }

    @Override
    public String toSql() {
        return sql;
    }

    private class QQ {
        final String rootEntity;
        final String rootAlias;
        final FieldExpressionResolverImpl selectFieldExpressionResolver;
        final FieldExpressionResolverImpl expressionResolver;
        final List<Func> selectFuncs;
        final List<PathExpressionAndAliasPair> fromPathExpressionAndAliasPairs;
        final List<Func> whereFuncs;
        final List<FieldExpressionAndOrderPair> orderByPairs;
        final List<FieldExpression> groupByExpressions;
        final List<Func> havingFuncs;
        final ImmutableList.Builder<Object> params = ImmutableList.builder();
        final ParamsBuilderImpl paramsBuilder = new ParamsBuilderImpl(params);
        final EntityMappingHelper helper;
        int aliasCount = 1;
        int relationAliasCount = 1;
        final Map<String, Map<String, PartAndJoinTpl>> partAndJoinTplMap = new LinkedHashMap<>();

        public QQ(String rootEntity, String rootAlias, FieldExpressionResolverImpl selectFieldExpressionResolver, FieldExpressionResolverImpl expressionResolver, List<Func> selectFuncs, List<PathExpressionAndAliasPair> fromPathExpressionAndAliasPairs, List<Func> whereFuncs, List<FieldExpressionAndOrderPair> orderByPairs, List<FieldExpression> groupByExpressions, List<Func> havingFuncs, EntityMappingHelper helper) {
            Objects.requireNonNull(rootEntity);
            Objects.requireNonNull(rootAlias);
            Objects.requireNonNull(selectFieldExpressionResolver);
            Objects.requireNonNull(expressionResolver);
            Objects.requireNonNull(selectFuncs);
            Objects.requireNonNull(fromPathExpressionAndAliasPairs);
            Objects.requireNonNull(whereFuncs);
            Objects.requireNonNull(orderByPairs);
            Objects.requireNonNull(groupByExpressions);
            Objects.requireNonNull(havingFuncs);
            this.rootEntity = rootEntity;
            this.rootAlias = rootAlias;
            this.selectFieldExpressionResolver = selectFieldExpressionResolver;
            this.expressionResolver = expressionResolver;
            this.selectFuncs = selectFuncs;
            this.fromPathExpressionAndAliasPairs = fromPathExpressionAndAliasPairs;
            this.whereFuncs = whereFuncs;
            this.orderByPairs = orderByPairs;
            this.groupByExpressions = groupByExpressions;
            this.havingFuncs = havingFuncs;
            this.helper = helper;
        }

        public String toSql() {

            final Map<FieldExpression, AliasAndColumn> fieldExpressionToAliasAndColumnMap = createFieldExpressionToAliasAndColumnMap();

            final Map<FieldExpression, FieldExpressionHolderFunc> funcMap = funcMap(fieldExpressionToAliasAndColumnMap);

            selectFieldExpressionResolver.setFuncMap(
                funcMap
            );
            expressionResolver.setFuncMap(
                funcMap
            );

            final StringBuilder builder = new StringBuilder();

            String sql = select(selectFuncs, paramsBuilder).toSql();

            builder.append("select ").append(sql);

            sql = from(partAndJoinTplMap).toSql();

            builder.append(" from ").append(sql);

            sql = where(whereFuncs, paramsBuilder).toSql();

            if (not(sql.trim().isEmpty())) {
                builder.append(" where ").append(sql);
            }

            sql = having(havingFuncs, paramsBuilder).toSql();

            if (not(sql.trim().isEmpty())) {
                builder.append(" having " + sql);
            }

            sql = orderBy(orderByPairs, fieldExpressionToAliasAndColumnMap).toSql();

            if (not(sql.trim().isEmpty())) {
                builder.append(" order by ").append(sql);
            }

            sql = groupBy(groupByExpressions, fieldExpressionToAliasAndColumnMap).toSql();

            if (not(sql.trim().isEmpty())) {
                builder.append(" group by " + sql);
            }

            return builder.toString();
        }

        private Map<FieldExpression, FieldExpressionHolderFunc> funcMap(Map<FieldExpression, AliasAndColumn> fieldExpressionToAliasAndColumnMap) {
            final ImmutableMap.Builder<FieldExpression, FieldExpressionHolderFunc> funcMapBuilder = ImmutableMap.builder();

            fieldExpressionToAliasAndColumnMap.forEach(
                (fieldExpression, aliasAndColumn) -> funcMapBuilder
                    .put(fieldExpression, new FieldExpressionHolderFuncImpl2(aliasAndColumn.toSql()))
            );
            return funcMapBuilder.build();
        }

        private ImmutableMap<FieldExpression, AliasAndColumn> createFieldExpressionToAliasAndColumnMap() {

            final Map<String, PathExpression> aliasToFullPathExpressionMap = new CC(rootAlias, rootEntity, fromPathExpressionAndAliasPairs)
                .getAliasToFullPathExpressionMap();

            final ImmutableMap.Builder<String, String> aliasToEntityMapBuilder = ImmutableMap.builder();

            aliasToEntityMapBuilder.put(rootAlias, rootEntity);

            Map<String, Optional<JoinType>> aliasToJoinTypeMap = toAliasToJoinTypeMap(fromPathExpressionAndAliasPairs);

            final Map<String, PartAndJoinTpl> rootMap = new LinkedHashMap<>();
            partAndJoinTplMap.put(rootAlias, rootMap);

            aliasToFullPathExpressionMap.forEach((alias, pathExpression) -> {

                partAndJoinTplMap.put(alias, new LinkedHashMap<>());

                final String[] parts = pathExpression.parts();
                Map<String, PartAndJoinTpl> tplMap = rootMap;
                String entity = rootEntity;
                String entityAlias = rootAlias;
                for (int i = 1, end = parts.length - 1; i < end; i++) {
                    String childEntityField = parts[i];

                    PartAndJoinTpl partAndJoinTpl = tplMap.get(childEntityField);

                    if (partAndJoinTpl == null) {

                        String childEntity = getChildEntity(entity, childEntityField);

                        final String childAlias = createAlias();

                        tplMap.put(
                            childEntityField,
                            partAndJoinTpl = new PartAndJoinTpl(
                                new JoinTpl(entityAlias, childAlias, entity, childEntityField, childEntity, Optional.empty())
                            )
                        );
                    }

                    entity = partAndJoinTpl.joinTpl.getChildEntity();
                    entityAlias = partAndJoinTpl.joinTpl.getChildEntityAlias();
                    tplMap = partAndJoinTpl.partAndJoinTplMap;
                }

                String childEntityField = parts[parts.length - 1];

                PartAndJoinTpl partAndJoinTpl = tplMap.get(childEntityField);

                if (partAndJoinTpl == null) {

                    String childEntity = getChildEntity(entity, childEntityField);

                    tplMap.put(
                        childEntityField,
                        partAndJoinTpl = new PartAndJoinTpl(
                            new JoinTpl(entityAlias, alias, entity, childEntityField, childEntity, aliasToJoinTypeMap.getOrDefault(alias, Optional.empty()))
                        )
                    );
                }

                aliasToEntityMapBuilder.put(alias, partAndJoinTpl.joinTpl.getChildEntity());
            });

            final ImmutableMap<String, String> aliasToEntityMap = aliasToEntityMapBuilder.build();
            final ImmutableMap.Builder<FieldExpression, AliasAndColumn> fieldExpToAliasedColumnMapBuilder = ImmutableMap.builder();

            selectFieldExpressionResolver.getFuncMap().keySet()
                .forEach(fieldExpression -> applyFieldExp(fieldExpression, aliasToEntityMap, fieldExpToAliasedColumnMapBuilder));

            expressionResolver.getFuncMap().keySet()
                .forEach(fieldExpression -> applyFieldExp(fieldExpression, aliasToEntityMap, fieldExpToAliasedColumnMapBuilder));

            return fieldExpToAliasedColumnMapBuilder.build();
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

            final String[] parts = pathExpression.parts();

            Map<String, PartAndJoinTpl> tplMap = partAndJoinTplMap.get(alias);
            String entity = aliasToEntityMap.get(alias);
            String entityAlias = alias;

            for (int i = 1, end = parts.length; i < end; i++) {
                String childEntityField = parts[i];

                PartAndJoinTpl partAndJoinTpl = tplMap.get(childEntityField);

                if (partAndJoinTpl == null) {

                    String childEntity = getChildEntity(entity, childEntityField);

                    final String childAlias = createAlias();

                    tplMap.put(
                        childEntityField,
                        partAndJoinTpl = new PartAndJoinTpl(
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

        private Map<String, Optional<JoinType>> toAliasToJoinTypeMap(List<PathExpressionAndAliasPair> fromPathExpressionAndAliasPairs) {
            final ImmutableMap.Builder<String, Optional<JoinType>> mapBuilder = ImmutableMap.builder();

            fromPathExpressionAndAliasPairs.forEach(pair -> {
                mapBuilder.put(pair.getAlias(), pair.getJoinType());
            });

            return mapBuilder.build();
        }

        private SelectClauseHandlerImpl select(List<Func> selectFuncs, ParamsBuilderImpl paramsBuilder) {
            return new SelectClauseHandlerImpl(
                selectFuncs,
                paramsBuilder
            );
        }

        private WhereClauseHandlerImpl where(List<Func> whereFuncs, ParamsBuilderImpl paramsBuilder) {
            return new WhereClauseHandlerImpl(
                whereFuncs,
                paramsBuilder
            );
        }

        private HavingClauseHandlerImpl having(List<Func> havingFuncs, ParamsBuilderImpl paramsBuilder) {
            return new HavingClauseHandlerImpl(
                havingFuncs,
                paramsBuilder
            );
        }

        private GroupByHandler groupBy(List<FieldExpression> groupByExpressions, Map<FieldExpression, AliasAndColumn> fieldExpressionToAliasAndColumnMap) {

            final ImmutableList.Builder<ColumnAliasPair> columnAliasPairListBuilder = ImmutableList.builder();

            groupByExpressions.forEach(fieldExpression -> {

                final String alias = fieldExpression.getParentPath().root();

                final AliasAndColumn aliasAndColumn = fieldExpressionToAliasAndColumnMap.get(alias);

                if (aliasAndColumn == null) {
                    throw new QueryParserException("Invalid fieldExpression '" + fieldExpression + "' in group by clause");
                }

                columnAliasPairListBuilder.add(
                    new ColumnAliasPair(alias, aliasAndColumn.getColumn())
                );
            });

            return new GroupByHandlerImpl(
                columnAliasPairListBuilder.build()
            );
        }

        private OrderByHandlerImpl orderBy(List<FieldExpressionAndOrderPair> orderByPairs, Map<FieldExpression, AliasAndColumn> fieldExpressionToAliasAndColumnMap) {

            final ImmutableList.Builder<OrderByData> orderByDataListBuilder = ImmutableList.builder();

            orderByPairs.forEach(pair -> {

                AliasAndColumn aliasAndColumn = fieldExpressionToAliasAndColumnMap.get(pair.getFieldExpression());

                if (aliasAndColumn == null) {
                    throw new QueryParserException("Invalid field expression '" + pair.getFieldExpression() + "' in order by clause");
                }

                orderByDataListBuilder.add(
                    new OrderByData(
                        aliasAndColumn.getAlias(),
                        aliasAndColumn.getColumn(),
                        pair.getOrder()
                    )
                );
            });

            return new OrderByHandlerImpl(
                orderByDataListBuilder.build()
            );
        }

        private FromClauseHandler from(Map<String, Map<String, PartAndJoinTpl>> joinTplsMap) {

            final ImmutableList.Builder<JoinData> joinDataListBuilder = ImmutableList.builder();

            joinTplsMap.forEach((alias, partAndJoinTplMap) -> {

                traverseRecursive(partAndJoinTplMap, joinDataListBuilder);
            });

            return new FromClauseHandlerImpl(
                ImmutableList.of(
                    new JoinClauseHandlerImpl(
                        new TableAliasPair(helper.getTable(rootEntity), rootAlias),
                        joinDataListBuilder.build()
                    )
                )
            );
        }

        private void traverseRecursive(Map<String, PartAndJoinTpl> partAndJoinTplMap, ImmutableList.Builder<JoinData> joinDataListBuilder) {
            partAndJoinTplMap.forEach((field, partAndJoinTpl) -> {
                traverseRecursive(partAndJoinTpl.partAndJoinTplMap, joinDataListBuilder);
                joinDataListBuilder.addAll(
                    createJoinData(partAndJoinTpl.joinTpl)
                );
            });
        }

        private List<JoinData> createJoinData(JoinTpl joinTpl) {

            Field field = helper.getField(joinTpl.getParentEntity(), joinTpl.getChildEntityField());

            Relationship relationship = field.getRelationship().orElseThrow(() -> new QueryParserException("No child '" + joinTpl.getChildEntity() + "' found in " + joinTpl.getParentEntity() + "." + joinTpl.getChildEntityField()));

            if (not(relationship.getEntity().equals(joinTpl.getChildEntity()))) {
                throw new QueryParserException("No child '" + joinTpl.getChildEntity() + "' found in " + joinTpl.getParentEntity() + "." + joinTpl.getChildEntityField());
            }

            DbColumnMapping columnMapping = helper.getColumnMapping(joinTpl.getParentEntity(), joinTpl.getChildEntityField());

            switch (columnMapping.getColumnType()) {

                case DIRECT:
                    return directColumnMapping((DirectColumnMapping) columnMapping, joinTpl);

                case INDIRECT:
                    return indirectColumnMapping((IndirectColumnMapping) columnMapping, joinTpl);

                case VIRTUAL:
                    return virtualColumnMapping((VirtualColumnMapping) columnMapping, joinTpl);
            }

            throw new QueryParserException("Invalid or no relationship between parent '" + joinTpl.getParentEntity() + "' and child '" + joinTpl.getChildEntity() + "'");
        }

        private List<JoinData> virtualColumnMapping(VirtualColumnMapping columnMapping, JoinTpl joinTpl) {

            ImmutableList.Builder<ColumnToColumnMapping> listBuilder = ImmutableList.builder();

            columnMapping.getForeignColumnMappingList().forEach(foreignColumnMapping -> {
                listBuilder.add(
                    new ColumnToColumnMapping(
                        foreignColumnMapping.getDstColumn().getName(),
                        foreignColumnMapping.getSrcColumn().getName()
                    )
                );
            });

            return ImmutableList.of(
                new JoinData(
                    joinTpl.getParentEntityAlias(),
                    joinTpl.getJoinType().orElse(JoinType.INNER_JOIN),
                    helper.getTable(joinTpl.getChildEntity()),
                    joinTpl.getChildEntityAlias(),
                    listBuilder.build()
                )
            );
        }

        private List<JoinData> indirectColumnMapping(IndirectColumnMapping columnMapping, JoinTpl joinTpl) {

            ImmutableList.Builder<JoinData> joinDataListBuilder = ImmutableList.builder();

            ImmutableList.Builder<ColumnToColumnMapping> mappingBuilder1 = ImmutableList.builder();

            columnMapping.getSrcForeignColumnMappingList().forEach(foreignColumnMapping -> {
                mappingBuilder1.add(
                    new ColumnToColumnMapping(
                        foreignColumnMapping.getSrcColumn().getName(),
                        foreignColumnMapping.getDstColumn().getName()
                    )
                );
            });

            String relationTableAlias = createRelationTableAlias();

            joinDataListBuilder.add(
                new JoinData(
                    joinTpl.getParentEntityAlias(),
                    joinTpl.getJoinType().orElse(JoinType.INNER_JOIN),
                    columnMapping.getRelationTable(),
                    relationTableAlias,
                    mappingBuilder1.build()
                )
            );

            ImmutableList.Builder<ColumnToColumnMapping> mappingBuilder2 = ImmutableList.builder();

            columnMapping.getDstForeignColumnMappingList().forEach(foreignColumnMapping -> {
                mappingBuilder2.add(
                    new ColumnToColumnMapping(
                        foreignColumnMapping.getDstColumn().getName(),
                        foreignColumnMapping.getSrcColumn().getName()
                    )
                );
            });

            joinDataListBuilder.add(
                new JoinData(
                    relationTableAlias,
                    joinTpl.getJoinType().orElse(JoinType.INNER_JOIN),
                    helper.getDbMapping(joinTpl.getChildEntity()).getTable(),
                    joinTpl.getChildEntityAlias(),
                    mappingBuilder2.build()
                )
            );

            return joinDataListBuilder.build();
        }

        private String createRelationTableAlias() {
            return "r" + String.valueOf(relationAliasCount++);
        }

        private List<JoinData> directColumnMapping(DirectColumnMapping directColumnMapping, JoinTpl joinTpl) {
            ImmutableList.Builder<ColumnToColumnMapping> mappingListBuilder = ImmutableList.builder();

            directColumnMapping.getForeignColumnMappingList().forEach(foreignColumnMapping -> {
                mappingListBuilder.add(
                    new ColumnToColumnMapping(
                        foreignColumnMapping.getSrcColumn().getName(),
                        foreignColumnMapping.getDstColumn().getName()
                    )
                );
            });

            return ImmutableList.of(
                new JoinData(
                    joinTpl.getParentEntityAlias(),
                    joinTpl.getJoinType().orElse(JoinType.INNER_JOIN),
                    helper.getEntity(joinTpl.getChildEntity()).getDbMapping().getTable(),
                    joinTpl.getChildEntityAlias(),
                    mappingListBuilder.build()
                )
            );
        }

        private String getColumn(String entity, String field) {

            final DbColumnMapping columnMapping = helper.getColumnMapping(entity, field);

            if (columnMapping.getColumnType() != ColumnType.SIMPLE) {
                throw new QueryParserException(entity + "." + field + " does not map to simple column type");
            }

            return ((SimpleColumnMapping) columnMapping).getColumn();
        }

        private String getChildEntity(String entity, String childEntityField) {

            Field field = helper.getField(entity, childEntityField);

            return field.getRelationship().orElseThrow(() -> new QueryParserException("No relationship found in " + entity + "." + childEntityField)).getEntity();
        }

        private void putParts(Map<String, String> entityToAliasMap, String[] parts, int startIndex, int endIndex) {
            for (int i = startIndex; i < endIndex; i++) {
                final String entity = parts[i];

                if (not(helper.exists(entity))) {
                    throw new QueryParserException("No entity found for name " + entity);
                }

                entityToAliasMap.put(
                    entity,
                    createAlias()
                );
            }
        }

        private String createAlias() {
            return "a" + String.valueOf(aliasCount++);
        }
    }

    private class CC {
        final String rootAlias;
        final String rootEntity;
        final List<PathExpressionAndAliasPair> fromPathExpressionAndAliasPairs;
        final Map<String, PathExpression> map;

        public CC(String rootAlias, String rootEntity, List<PathExpressionAndAliasPair> fromPathExpressionAndAliasPairs) {
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

                if (!map.containsKey(pathExpression.root())) {

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

        private PathExpression fullPathExpression(ImmutableList<PathExpression> list) {

            List<String> partList = new ArrayList<>();
            partList.add(rootAlias);

            list.forEach(pathExpression -> {
                final String[] parts = pathExpression.parts();
                for (int i = 1; i < parts.length; i++) {
                    partList.add(parts[i]);
                }
            });

            return new PathExpressionImpl(partList.toArray(new String[partList.size()]));
        }
    }

    public static void main(String[] asdf) {

    }

    private final static class PartAndJoinTpl {
        final JoinTpl joinTpl;
        final Map<String, PartAndJoinTpl> partAndJoinTplMap = new HashMap<>();

        private PartAndJoinTpl(JoinTpl joinTpl) {
            Objects.requireNonNull(joinTpl);
            this.joinTpl = joinTpl;
        }
    }
}
