package elasta.orm.query.expression.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.criteria.Func;
import elasta.criteria.funcs.ParamsBuilderImpl;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.query.ex.QueryParserException;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.FromClauseHandler;
import elasta.orm.query.expression.GroupByHandler;
import elasta.orm.query.expression.builder.FieldExpressionAndOrderPair;
import elasta.orm.query.expression.core.*;
import elasta.sql.core.SqlAndParams;
import io.vertx.core.json.JsonArray;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static elasta.commons.Utils.not;

/**
 * Created by sohan on 4/10/2017.
 */
final public class SqlAndParamsBuilder {

    final String rootEntity;
    final String rootAlias;
    final List<Func> selectFuncs;
    final List<Func> whereFuncs;
    final List<FieldExpressionAndOrderPair> orderByPairs;
    final List<FieldExpression> groupByExpressions;
    final List<Func> havingFuncs;
    final EntityMappingHelper helper;

    final ImmutableList.Builder<Object> paramsListBuilder = ImmutableList.builder();
    final ParamsBuilderImpl paramsBuilder = new ParamsBuilderImpl(paramsListBuilder);

    final Map<String, Map<String, QueryImpl.PartAndJoinTpl>> aliasToJoinTplMap;
    final AliasGenerator aliasGenerator;

    final Map<FieldExpression, AliasAndColumn> fieldExpressionToAliasAndColumnMap;

    final JoinTplToJoinDataConverter joinTplToJoinDataConverter;

    public SqlAndParamsBuilder(String rootEntity, String rootAlias, List<Func> selectFuncs, List<Func> whereFuncs, List<FieldExpressionAndOrderPair> orderByPairs, List<FieldExpression> groupByExpressions, List<Func> havingFuncs, EntityMappingHelper helper, Map<String, Map<String, QueryImpl.PartAndJoinTpl>> aliasToJoinTplMap, AliasGenerator aliasGenerator, Map<FieldExpression, AliasAndColumn> fieldExpressionToAliasAndColumnMap) {
        this.rootEntity = rootEntity;
        this.rootAlias = rootAlias;
        this.selectFuncs = selectFuncs;
        this.whereFuncs = whereFuncs;
        this.orderByPairs = orderByPairs;
        this.groupByExpressions = groupByExpressions;
        this.havingFuncs = havingFuncs;
        this.helper = helper;
        this.aliasToJoinTplMap = aliasToJoinTplMap;
        this.aliasGenerator = aliasGenerator;
        this.fieldExpressionToAliasAndColumnMap = fieldExpressionToAliasAndColumnMap;

        this.joinTplToJoinDataConverter = new JoinTplToJoinDataConverter(
            helper,
            aliasGenerator
        );
    }

    public SqlAndParams build() {
        return new SqlAndParams(
            toSql(),
            new JsonArray(
                paramsListBuilder.build()
            )
        );
    }

    public String toSql() {

        final StringBuilder builder = new StringBuilder();

        String sql = select(selectFuncs, paramsBuilder).toSql();

        builder.append("select ").append(sql);

        sql = from(aliasToJoinTplMap).toSql();

        builder.append(" from ").append(sql);

        sql = where(whereFuncs, paramsBuilder).toSql();

        if (not(sql.trim().isEmpty())) {
            builder.append(" where ").append(sql);
        }

        sql = having(havingFuncs, paramsBuilder).toSql();

        if (not(sql.trim().isEmpty())) {
            builder.append(" having " + sql);
        }

        sql = orderBy(orderByPairs).toSql();

        if (not(sql.trim().isEmpty())) {
            builder.append(" order by ").append(sql);
        }

        sql = groupBy(groupByExpressions).toSql();

        if (not(sql.trim().isEmpty())) {
            builder.append(" group by " + sql);
        }

        return builder.toString();
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

    private GroupByHandler groupBy(List<FieldExpression> groupByExpressions) {

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

    private OrderByHandlerImpl orderBy(List<FieldExpressionAndOrderPair> orderByPairs) {

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

    private FromClauseHandler from(Map<String, Map<String, QueryImpl.PartAndJoinTpl>> joinTplsMap) {

        return new FromClauseHandlerImpl(
            ImmutableList.of(
                new JoinClauseHandlerImpl(
                    new TableAliasPair(helper.getTable(rootEntity), rootAlias),
                    generateJoinData(
                        new JoinTplMapBuilder(joinTplsMap, joinTplToJoinDataConverter).build()
                    )
                )
            )
        );
    }

    private Collection<JoinData> generateJoinData(Map<String, JoinData> aliasToJoinDataMap) {

        return new JoinDataBuilder(
            rootAlias, aliasToJoinDataMap
        ).build();
    }
}
