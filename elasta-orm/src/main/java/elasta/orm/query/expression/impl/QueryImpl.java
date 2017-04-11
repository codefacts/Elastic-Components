package elasta.orm.query.expression.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.core.promise.intfs.Promise;
import elasta.criteria.Func;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.query.expression.*;
import elasta.orm.query.expression.builder.FieldExpressionAndOrderPair;
import elasta.orm.query.expression.builder.FieldExpressionHolderFunc;
import elasta.orm.query.expression.builder.impl.PathExpressionAndAliasPair;
import elasta.orm.query.expression.core.*;
import elasta.orm.query.read.ObjectReader;
import elasta.orm.query.read.builder.ObjectReaderBuilderImpl;
import elasta.sql.SqlExecutor;
import elasta.orm.query.expression.builder.FieldExpressionResolverImpl;
import elasta.sql.core.SqlAndParams;
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
        final String ALIAS_STR = "a";
        final AliasCounter aliasCounter = new AliasCounter(1);

        public Promise<List<JsonObject>> execute() {

            Map<String, PathExpression> aliasToFullPathExpressionMap = new PathExpTranslator(rootAlias, rootEntity, fromPathExpressionAndAliasPairs)
                .getAliasToFullPathExpressionMap();

            Tpl2 tpl2 = fieldExpressionToAliasAndColumnMap(aliasToFullPathExpressionMap);

            SqlAndParams sqlAndParams = new SqlAndParamsBuilder(
                rootEntity,
                rootAlias,
                selectFuncs,
                whereFuncs,
                orderByPairs,
                groupByExpressions,
                havingFuncs,
                helper,
                tpl2.getAliasToJoinTplMap(),
                ALIAS_STR,
                aliasCounter,
                tpl2.getFieldExpToAliasedColumnMap()
            ).build();

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

            Tpl2 tpl2 = fieldExpressionToAliasAndColumnMap(aliasToFullPathExpressionMap);

            SqlAndParams sqlAndParams = new SqlAndParamsBuilder(
                rootEntity,
                rootAlias,
                selectFuncs,
                whereFuncs,
                orderByPairs,
                groupByExpressions,
                havingFuncs,
                helper,
                tpl2.getAliasToJoinTplMap(),
                ALIAS_STR,
                aliasCounter,
                tpl2.getFieldExpToAliasedColumnMap()
            ).build();

            return sqlExecutor.query(sqlAndParams.getSql(), sqlAndParams.getParams())
                .map(ResultSet::getResults);
        }

        private Tpl2 fieldExpressionToAliasAndColumnMap(Map<String, PathExpression> aliasToFullPathExpressionMap) {

            Tpl2 tpl2 = new FieldExpressionToAliasAndColumnMapTranslator(
                rootEntity,
                rootAlias,
                selectFieldExpressionResolver,
                expressionResolver,
                fromPathExpressionAndAliasPairs,
                helper,
                ALIAS_STR,
                aliasCounter

            ).translate(aliasToFullPathExpressionMap);

            final Map<FieldExpression, AliasAndColumn> fieldExpressionToAliasAndColumnMap = tpl2.getFieldExpToAliasedColumnMap();

            final Map<FieldExpression, FieldExpressionHolderFunc> funcMap = funcMap(fieldExpressionToAliasAndColumnMap);

            selectFieldExpressionResolver.setFuncMap(
                funcMap
            );
            expressionResolver.setFuncMap(
                funcMap
            );

            return tpl2;
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

    public static class AliasCounter {
        public int aliasCount;

        public AliasCounter(int aliasCount) {
            this.aliasCount = aliasCount;
        }
    }

    public static void main(String[] asdf) {

    }
}
