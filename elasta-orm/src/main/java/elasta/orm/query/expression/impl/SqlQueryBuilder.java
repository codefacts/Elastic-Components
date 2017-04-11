package elasta.orm.query.expression.impl;

import com.google.common.collect.ImmutableList;
import elasta.criteria.Func;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.query.ex.QueryParserException;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.builder.FieldExpressionAndOrderPair;
import elasta.orm.query.expression.core.*;
import elasta.sql.core.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by sohan on 4/10/2017.
 */
final public class SqlQueryBuilder {

    final String rootEntity;
    final String rootAlias;
    final List<Func> selectFuncs;
    final List<Func> whereFuncs;
    final List<FieldExpressionAndOrderPair> orderByPairs;
    final List<FieldExpression> groupByExpressions;
    final List<Func> havingFuncs;
    final EntityMappingHelper helper;

    final Map<String, Map<String, QueryImpl.PartAndJoinTpl>> aliasToJoinTplMap;
    final AliasGenerator aliasGenerator;

    final Map<FieldExpression, AliasAndColumn> fieldExpressionToAliasAndColumnMap;

    final JoinTplToJoinDataConverter joinTplToJoinDataConverter;

    public SqlQueryBuilder(String rootEntity, String rootAlias, List<Func> selectFuncs, List<Func> whereFuncs, List<FieldExpressionAndOrderPair> orderByPairs, List<FieldExpression> groupByExpressions, List<Func> havingFuncs, EntityMappingHelper helper, Map<String, Map<String, QueryImpl.PartAndJoinTpl>> aliasToJoinTplMap, AliasGenerator aliasGenerator, Map<FieldExpression, AliasAndColumn> fieldExpressionToAliasAndColumnMap) {
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

    public SqlQuery build() {
        return new SqlQuery(
            selectFuncs,
            new TableAliasPair(helper.getTable(rootEntity), rootAlias),
            generateJoinData(
                new JoinTplMapBuilder(aliasToJoinTplMap, joinTplToJoinDataConverter).build()
            ),
            whereFuncs,
            havingFuncs,
            orderBy(orderByPairs),
            groupBy(groupByExpressions)
        );
    }

    private ImmutableList<ColumnAliasPair> groupBy(List<FieldExpression> groupByExpressions) {

        final ImmutableList.Builder<ColumnAliasPair> columnAliasPairListBuilder = ImmutableList.builder();

        groupByExpressions.forEach(fieldExpression -> {

            final AliasAndColumn aliasAndColumn = fieldExpressionToAliasAndColumnMap.get(fieldExpression);

            if (aliasAndColumn == null) {
                throw new QueryParserException("Invalid fieldExpression '" + fieldExpression + "' in group by clause");
            }

            columnAliasPairListBuilder.add(
                new ColumnAliasPair(aliasAndColumn.getAlias(), aliasAndColumn.getColumn())
            );
        });

        return columnAliasPairListBuilder.build();
    }

    private ImmutableList<OrderByData> orderBy(List<FieldExpressionAndOrderPair> orderByPairs) {

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

        return orderByDataListBuilder.build();
    }

    private Collection<JoinData> generateJoinData(Map<String, JoinData> aliasToJoinDataMap) {

        return new JoinDataBuilder(
            rootAlias, aliasToJoinDataMap
        ).build();
    }
}
