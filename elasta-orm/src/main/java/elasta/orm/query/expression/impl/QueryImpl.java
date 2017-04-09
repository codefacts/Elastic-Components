package elasta.orm.query.expression.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.core.promise.intfs.Promise;
import elasta.criteria.Func;
import elasta.criteria.funcs.ParamsBuilderImpl;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.Field;
import elasta.orm.entity.core.Relationship;
import elasta.orm.entity.core.columnmapping.*;
import elasta.orm.query.expression.*;
import elasta.orm.query.expression.builder.FieldExpressionAndOrderPair;
import elasta.orm.query.expression.builder.FieldExpressionHolderFunc;
import elasta.orm.query.expression.builder.impl.PathExpressionAndAliasPair;
import elasta.orm.query.expression.core.*;
import elasta.orm.query.ex.QueryParserException;
import elasta.orm.query.read.ObjectReader;
import elasta.orm.query.read.builder.ObjectReaderBuilderImpl;
import elasta.orm.upsert.ColumnToColumnMapping;
import elasta.sql.SqlExecutor;
import elasta.sql.core.JoinType;
import elasta.orm.query.expression.builder.FieldExpressionResolverImpl;
import elasta.sql.core.SqlAndParams;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.util.*;
import java.util.stream.Collectors;

import static elasta.commons.Utils.not;

/**
 * Created by Jango on 17/02/10.
 */
final public class QueryImpl implements Query {
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
    final EntityMappingHelper helper;
    final SqlExecutor sqlExecutor;

    public QueryImpl(String rootEntity, String rootAlias, FieldExpressionResolverImpl selectFieldExpressionResolver, FieldExpressionResolverImpl expressionResolver, List<Func> selectFuncs, List<PathExpressionAndAliasPair> fromPathExpressionAndAliasPairs, List<Func> whereFuncs, List<FieldExpressionAndOrderPair> orderByPairs, List<FieldExpression> groupByExpressions, List<Func> havingFuncs, EntityMappingHelper helper, SqlExecutor sqlExecutor) {
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
        Objects.requireNonNull(helper);
        Objects.requireNonNull(sqlExecutor);
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
        this.sqlExecutor = sqlExecutor;
    }

    @Override
    public Promise<List<JsonObject>> execute() {
        return new QQ().execute();
    }

    @Override
    public Promise<List<JsonArray>> executeArray() {
        return new QQ().executeArray();
    }

    private class QQ {
        final ImmutableList.Builder<Object> paramsListBuilder = ImmutableList.builder();
        final ParamsBuilderImpl paramsBuilder = new ParamsBuilderImpl(paramsListBuilder);
        final Map<String, Map<String, PartAndJoinTpl>> partAndJoinTplMap = new LinkedHashMap<>();
        final String ALIAS_STR = "a";
        final AliasCounter aliasCounter = new AliasCounter(1);

        public String toSql(Map<FieldExpression, AliasAndColumn> fieldExpressionToAliasAndColumnMap) {

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

        private Map<FieldExpression, AliasAndColumn> fieldExpressionToAliasAndColumnMap(Map<String, PathExpression> aliasToFullPathExpressionMap) {

            final Map<FieldExpression, AliasAndColumn> fieldExpressionToAliasAndColumnMap = new FieldExpressionToAliasAndColumnMapTranslator(
                rootEntity,
                rootAlias,
                selectFieldExpressionResolver,
                expressionResolver,
                fromPathExpressionAndAliasPairs,
                helper,
                partAndJoinTplMap,
                aliasCounter

            ).translate(aliasToFullPathExpressionMap);

            final Map<FieldExpression, FieldExpressionHolderFunc> funcMap = funcMap(fieldExpressionToAliasAndColumnMap);

            selectFieldExpressionResolver.setFuncMap(
                funcMap
            );
            expressionResolver.setFuncMap(
                funcMap
            );

            return fieldExpressionToAliasAndColumnMap;
        }

        private Map<FieldExpression, FieldExpressionHolderFunc> funcMap(Map<FieldExpression, AliasAndColumn> fieldExpressionToAliasAndColumnMap) {
            final ImmutableMap.Builder<FieldExpression, FieldExpressionHolderFunc> funcMapBuilder = ImmutableMap.builder();

            fieldExpressionToAliasAndColumnMap.forEach(
                (fieldExpression, aliasAndColumn) -> funcMapBuilder
                    .put(fieldExpression, new FieldExpressionHolderFuncImpl2(aliasAndColumn.toSql()))
            );
            return funcMapBuilder.build();
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

            final ImmutableMap.Builder<String, JoinTpl> joinTplMapBuilder = ImmutableMap.builder();

            joinTplsMap.forEach((alias, partAndJoinTplMap) -> {

                traverseRecursive(partAndJoinTplMap, joinTplMapBuilder);
            });

            return new FromClauseHandlerImpl(
                ImmutableList.of(
                    new JoinClauseHandlerImpl(
                        new TableAliasPair(helper.getTable(rootEntity), rootAlias),
                        generateJoinData(joinTplMapBuilder.build())
                    )
                )
            );
        }

        private List<JoinData> generateJoinData(ImmutableMap<String, JoinTpl> aliasToJoinTplMap) {

            return new JoinDataBuilder(rootAlias, aliasToJoinTplMap, this::createJoinData).build();
        }

        private void traverseRecursive(Map<String, PartAndJoinTpl> partAndJoinTplMap, final ImmutableMap.Builder<String, JoinTpl> joinTplMapBuilder) {
            partAndJoinTplMap.forEach((field, partAndJoinTpl) -> {
                traverseRecursive(partAndJoinTpl.partAndJoinTplMap, joinTplMapBuilder);
                joinTplMapBuilder.put(partAndJoinTpl.joinTpl.getChildEntityAlias(), partAndJoinTpl.joinTpl);
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
                    return directColumnMapping((DirectDbColumnMapping) columnMapping, joinTpl);

                case INDIRECT:
                    return indirectColumnMapping((IndirectDbColumnMapping) columnMapping, joinTpl);

                case VIRTUAL:
                    return virtualColumnMapping((VirtualDbColumnMapping) columnMapping, joinTpl);
            }

            throw new QueryParserException("Invalid or no relationship between parent '" + joinTpl.getParentEntity() + "' and child '" + joinTpl.getChildEntity() + "'");
        }

        private List<JoinData> virtualColumnMapping(VirtualDbColumnMapping columnMapping, JoinTpl joinTpl) {

            ImmutableList.Builder<ColumnToColumnMapping> listBuilder = ImmutableList.builder();

            columnMapping.getForeignColumnMappingList().forEach(foreignColumnMapping -> {
                listBuilder.add(
                    new ColumnToColumnMapping(
                        foreignColumnMapping.getDstColumn(),
                        foreignColumnMapping.getSrcColumn()
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

        private List<JoinData> indirectColumnMapping(IndirectDbColumnMapping columnMapping, JoinTpl joinTpl) {

            ImmutableList.Builder<JoinData> joinDataListBuilder = ImmutableList.builder();

            ImmutableList.Builder<ColumnToColumnMapping> mappingBuilder1 = ImmutableList.builder();

            columnMapping.getSrcForeignColumnMappingList().forEach(foreignColumnMapping -> {
                mappingBuilder1.add(
                    new ColumnToColumnMapping(
                        foreignColumnMapping.getSrcColumn(),
                        foreignColumnMapping.getDstColumn()
                    )
                );
            });

            String relationTableAlias = createAlias();

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
                        foreignColumnMapping.getDstColumn(),
                        foreignColumnMapping.getSrcColumn()
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

        private List<JoinData> directColumnMapping(DirectDbColumnMapping directDbColumnMapping, JoinTpl joinTpl) {
            ImmutableList.Builder<ColumnToColumnMapping> mappingListBuilder = ImmutableList.builder();

            directDbColumnMapping.getForeignColumnMappingList().forEach(foreignColumnMapping -> {
                mappingListBuilder.add(
                    new ColumnToColumnMapping(
                        foreignColumnMapping.getSrcColumn(),
                        foreignColumnMapping.getDstColumn()
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

        public Promise<List<JsonObject>> execute() {

            Map<String, PathExpression> aliasToFullPathExpressionMap = new PathExpTranslator(rootAlias, rootEntity, fromPathExpressionAndAliasPairs)
                .getAliasToFullPathExpressionMap();

            Map<FieldExpression, AliasAndColumn> fieldExpressionAliasAndColumnMap = fieldExpressionToAliasAndColumnMap(aliasToFullPathExpressionMap);

            SqlAndParams sqlAndParams = new SqlAndParams(
                toSql(
                    fieldExpressionAliasAndColumnMap
                ),
                new JsonArray(
                    paramsListBuilder.build()
                )
            );

            ObjectReader objectReader = new ObjectReaderBuilderImpl(
                rootAlias, rootEntity,
                fieldExpressions(),
                aliasToFullPathExpressionMap,
                helper
            ).build();

            return sqlExecutor.query(sqlAndParams.getSql(), sqlAndParams.getParams())
                .map(ResultSet::getResults)
                .map(
                    jsonArrays -> jsonArrays.stream()
                        .map(jsonArray -> objectReader.read(jsonArray, jsonArrays))
                        .collect(Collectors.toList())
                )
                ;
        }

        public Promise<List<JsonArray>> executeArray() {

            Map<String, PathExpression> aliasToFullPathExpressionMap = new PathExpTranslator(rootAlias, rootEntity, fromPathExpressionAndAliasPairs)
                .getAliasToFullPathExpressionMap();

            Map<FieldExpression, AliasAndColumn> fieldExpressionAliasAndColumnMap = fieldExpressionToAliasAndColumnMap(aliasToFullPathExpressionMap);

            SqlAndParams sqlAndParams = new SqlAndParams(
                toSql(
                    fieldExpressionAliasAndColumnMap
                ),
                new JsonArray(
                    paramsListBuilder.build()
                )
            );

            return sqlExecutor.query(sqlAndParams.getSql(), sqlAndParams.getParams())
                .map(ResultSet::getResults);
        }

        private List<FieldExpression> fieldExpressions() {

            return ImmutableList.copyOf(
                selectFieldExpressionResolver.getFuncMap().keySet()
            );
        }

        private String createAlias() {
            return ALIAS_STR + String.valueOf(aliasCounter.aliasCount++);
        }
    }

    final static class PartAndJoinTpl {
        final JoinTpl joinTpl;
        final Map<String, PartAndJoinTpl> partAndJoinTplMap = new HashMap<>();

        PartAndJoinTpl(JoinTpl joinTpl) {
            Objects.requireNonNull(joinTpl);
            this.joinTpl = joinTpl;
        }
    }

    static class AliasCounter {
        int aliasCount;

        public AliasCounter(int aliasCount) {
            this.aliasCount = aliasCount;
        }
    }

    public static void main(String[] asdf) {

    }
}
