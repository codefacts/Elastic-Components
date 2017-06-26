package elasta.orm.query.expression.impl;

import com.google.common.collect.ImmutableList;
import elasta.criteria.Func;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.ex.QueryParserException;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.builder.FieldExpressionAndOrderPair;
import elasta.orm.query.expression.core.*;
import elasta.sql.core.*;

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
    final QueryExecutor.Pagination pagination;
    final EntityMappingHelper helper;

    final Map<String, Map<String, QueryImpl.PartAndJoinTpl>> aliasToJoinTplMap;
    final AliasGenerator aliasGenerator;

    final Map<FieldExpression, AliasAndColumn> fieldExpressionToAliasAndColumnMap;

    final JoinTplToJoinDataConverter joinTplToJoinDataConverter;

    public SqlQueryBuilder(String rootEntity, String rootAlias, List<Func> selectFuncs, List<Func> whereFuncs, List<FieldExpressionAndOrderPair> orderByPairs, List<FieldExpression> groupByExpressions, List<Func> havingFuncs, QueryExecutor.Pagination pagination, EntityMappingHelper helper, Map<String, Map<String, QueryImpl.PartAndJoinTpl>> aliasToJoinTplMap, AliasGenerator aliasGenerator, Map<FieldExpression, AliasAndColumn> fieldExpressionToAliasAndColumnMap) {
        this.rootEntity = rootEntity;
        this.rootAlias = rootAlias;
        this.selectFuncs = selectFuncs;
        this.whereFuncs = whereFuncs;
        this.orderByPairs = orderByPairs;
        this.groupByExpressions = groupByExpressions;
        this.havingFuncs = havingFuncs;
        this.pagination = (pagination == null) ? null : pagination;
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
        return SqlQuery.builder()
            .selectFuncs(selectFuncs)
            .tableAliasPair(new TableAliasPair(helper.getTable(rootEntity), rootAlias))
            .joinDatas(new JoinTplMapBuilder(aliasToJoinTplMap, joinTplToJoinDataConverter).build())
            .whereFuncs(whereFuncs)
            .havingFuncs(havingFuncs)
            .orderByDatas(orderBy(orderByPairs))
            .groupBy(groupBy(groupByExpressions))
            .sqlPagination(pagination == null ? null : convertToPagination(pagination))
            .build();
    }

    private SqlPagination convertToPagination(QueryExecutor.Pagination pagination) {
        return SqlPagination.builder()
            .paginationColumn(toColumnAliasPair(pagination.getFieldExpression()))
            .offset(pagination.getOffset())
            .size(pagination.getSize())
            .build();
    }

    private ColumnAliasPair toColumnAliasPair(FieldExpression fieldExpression) {
        AliasAndColumn aliasAndColumn = aliasAndColumn(fieldExpression);
        return new ColumnAliasPair(aliasAndColumn.getAlias(), aliasAndColumn.getColumn());
    }

    private ImmutableList<ColumnAliasPair> groupBy(List<FieldExpression> groupByExpressions) {

        final ImmutableList.Builder<ColumnAliasPair> columnAliasPairListBuilder = ImmutableList.builder();

        groupByExpressions.forEach(fieldExpression -> {

            final AliasAndColumn aliasAndColumn = aliasAndColumn(fieldExpression);

            if (aliasAndColumn == null) {
                throw new QueryParserException("Invalid fieldExpression '" + fieldExpression + "' in group by clause");
            }

            columnAliasPairListBuilder.add(
                new ColumnAliasPair(aliasAndColumn.getAlias(), aliasAndColumn.getColumn())
            );
        });

        return columnAliasPairListBuilder.build();
    }

    private AliasAndColumn aliasAndColumn(FieldExpression fieldExpression) {
        return fieldExpressionToAliasAndColumnMap.get(fieldExpression);
    }

    private ImmutableList<OrderByData> orderBy(List<FieldExpressionAndOrderPair> orderByPairs) {

        final ImmutableList.Builder<OrderByData> orderByDataListBuilder = ImmutableList.builder();

        orderByPairs.forEach(pair -> {

            AliasAndColumn aliasAndColumn = aliasAndColumn(pair.getFieldExpression());

            if (aliasAndColumn == null) {
                throw new QueryParserException("Invalid column expression '" + pair.getFieldExpression() + "' in order by clause");
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
}
