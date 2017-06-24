package elasta.orm.query.expression.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.core.promise.intfs.Promise;
import elasta.criteria.Func;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.*;
import elasta.orm.query.expression.builder.FieldExpressionAndOrderPair;
import elasta.orm.query.expression.builder.FieldExpressionHolderFunc;
import elasta.orm.query.expression.builder.impl.PathExpressionAndAliasPair;
import elasta.orm.query.expression.core.*;
import elasta.orm.query.expression.ex.QueryException;
import elasta.orm.query.read.ObjectReader;
import elasta.orm.query.read.builder.ObjectReaderBuilderImpl;
import elasta.sql.SqlDB;
import elasta.orm.query.expression.builder.FieldExpressionResolverImpl;
import elasta.sql.core.SqlQuery;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.util.*;
import java.util.stream.Collectors;

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
    final QueryExecutor.Pagination pagination;
    final EntityMappingHelper helper;
    final SqlDB sqlDB;

    public QueryImpl(String rootEntity, String rootAlias, FieldExpressionResolverImpl selectFieldExpressionResolver, FieldExpressionResolverImpl expressionResolver, List<Func> selectFuncs, List<PathExpressionAndAliasPair> fromPathExpressionAndAliasPairs, List<Func> whereFuncs, List<FieldExpressionAndOrderPair> orderByPairs, List<FieldExpression> groupByExpressions, List<Func> havingFuncs, QueryExecutor.Pagination pagination, EntityMappingHelper helper, SqlDB sqlDB) {
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
        Objects.requireNonNull(sqlDB);
        this.rootEntity = rootEntity;
        this.rootAlias = rootAlias;
        this.selectFieldExpressionResolver = selectFieldExpressionResolver;
        this.expressionResolver = expressionResolver;
        this.selectFuncs = checkSelectFuns(selectFuncs);
        this.fromPathExpressionAndAliasPairs = fromPathExpressionAndAliasPairs;
        this.whereFuncs = whereFuncs;
        this.orderByPairs = orderByPairs;
        this.groupByExpressions = groupByExpressions;
        this.havingFuncs = havingFuncs;
        this.pagination = (pagination == null) ? null : pagination;
        this.helper = helper;
        this.sqlDB = sqlDB;
    }

    private List<Func> checkSelectFuns(List<Func> selectFuncs) {
        if (selectFuncs.isEmpty()) {
            throw new QueryException("Selections can not be empty");
        }
        return selectFuncs;
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
        final AliasGenerator aliasGenerator = new AliasGenerator("a");

        public Promise<List<JsonObject>> execute() {

            Map<String, PathExpression> aliasToFullPathExpressionMap = new AliasToFullPathExpressionBuilder(
                rootAlias, rootEntity,
                fromPathExpressionAndAliasPairs.stream()
            ).build();

            Tpl3 tpl3 = fieldExpressionToAliasAndColumnMap(aliasToFullPathExpressionMap);

            final Map<FieldExpression, AliasAndColumn> fieldExpressionToAliasAndColumnMap = combineAliasAndColumnMaps(
                tpl3.getSelectFieldExpressionToAliasAndColumnMap(),
                tpl3.getFieldExpToAliasAndColumnMap()
            );

            SqlQuery sqlQuery = new SqlQueryBuilder(
                rootEntity,
                rootAlias,
                selectFuncs,
                whereFuncs,
                orderByPairs,
                groupByExpressions,
                havingFuncs,
                pagination,
                helper,
                tpl3.getAliasToJoinTplMap(),
                aliasGenerator,
                fieldExpressionToAliasAndColumnMap
            ).build();

            ObjectReader objectReader = new ObjectReaderBuilderImpl(
                rootAlias, rootEntity,
                fieldExpressions(),
                aliasToFullPathExpressionMap,
                helper
            ).build();

            return sqlDB.query(sqlQuery)
                .map(ResultSet::getResults)
                .map(
                    jsonArrays -> jsonArrays.stream()
                        .map(jsonArray -> objectReader.read(jsonArray, jsonArrays))
                        .collect(Collectors.toList())
                )
                ;
        }

        private Map<FieldExpression, AliasAndColumn> combineAliasAndColumnMaps(Map<FieldExpression, AliasAndColumn> selectFieldExpressionToAliasAndColumnMap, Map<FieldExpression, AliasAndColumn> fieldExpToAliasedColumnMap) {
            HashMap<FieldExpression, AliasAndColumn> hashMap = new HashMap<>(selectFieldExpressionToAliasAndColumnMap);
            hashMap.putAll(fieldExpToAliasedColumnMap);
            return Collections.unmodifiableMap(hashMap);
        }

        public Promise<List<JsonArray>> executeArray() {

            Map<String, PathExpression> aliasToFullPathExpressionMap = new AliasToFullPathExpressionBuilder(
                rootAlias, rootEntity,
                fromPathExpressionAndAliasPairs.stream()
            ).build();

            Tpl3 tpl3 = fieldExpressionToAliasAndColumnMap(aliasToFullPathExpressionMap);

            final Map<FieldExpression, AliasAndColumn> fieldExpressionToAliasAndColumnMap = ImmutableMap.<FieldExpression, AliasAndColumn>builder()
                .putAll(
                    tpl3.getSelectFieldExpressionToAliasAndColumnMap()
                )
                .putAll(
                    tpl3.getFieldExpToAliasAndColumnMap()
                )
                .build();

            SqlQuery sqlQuery = new SqlQueryBuilder(
                rootEntity,
                rootAlias,
                selectFuncs,
                whereFuncs,
                orderByPairs,
                groupByExpressions,
                havingFuncs,
                pagination,
                helper,
                tpl3.getAliasToJoinTplMap(),
                aliasGenerator,
                fieldExpressionToAliasAndColumnMap
            ).build();

            return sqlDB
                .query(sqlQuery)
                .map(ResultSet::getResults);
        }

        private Tpl3 fieldExpressionToAliasAndColumnMap(Map<String, PathExpression> aliasToFullPathExpressionMap) {

            Tpl3 tpl3 = new FieldExpressionToAliasAndColumnMapBuilder(
                rootEntity,
                rootAlias,
                selectFieldExpressionResolver,
                expressionResolver,
                groupByExpressions,
                fromPathExpressionAndAliasPairs,
                helper,
                aliasGenerator

            ).build(aliasToFullPathExpressionMap);

            selectFieldExpressionResolver.setFuncMap(
                funcMap(tpl3.getSelectFieldExpressionToAliasAndColumnMap())
            );

            expressionResolver.setFuncMap(
                funcMap(tpl3.getFieldExpToAliasAndColumnMap())
            );

            return tpl3;
        }

        private Map<FieldExpression, FieldExpressionHolderFunc> funcMap(Map<FieldExpression, AliasAndColumn> fieldExpressionToAliasAndColumnMap) {
            final ImmutableMap.Builder<FieldExpression, FieldExpressionHolderFunc> funcMapBuilder = ImmutableMap.builder();

            fieldExpressionToAliasAndColumnMap.forEach(
                (fieldExpression, aliasAndColumn) -> funcMapBuilder
                    .put(fieldExpression, new FieldExpressionHolderFuncImpl2(aliasAndColumn.toSql()))
            );
            return funcMapBuilder.build();
        }

        private List<FieldExpression> fieldExpressions() {

            return ImmutableList.copyOf(
                selectFieldExpressionResolver.getFuncMap().keySet()
            );
        }
    }

    final static class PartAndJoinTpl {
        final JoinTpl joinTpl;
        final Map<String, PartAndJoinTpl> partToJoinTplMap = new LinkedHashMap<>();

        PartAndJoinTpl(JoinTpl joinTpl) {
            Objects.requireNonNull(joinTpl);
            this.joinTpl = joinTpl;
        }
    }

    public static void main(String[] asdf) {

    }
}
