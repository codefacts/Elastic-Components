package elasta.orm.query.expression.builder.impl;

import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.Query;
import elasta.orm.query.expression.builder.ex.FieldExpressionResolverException;
import elasta.orm.query.expression.impl.QueryImpl;
import elasta.orm.query.expression.builder.*;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import elasta.sql.SqlDB;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * Created by Jango on 17/02/09.
 */
final public class QueryBuilderImpl implements QueryBuilder {
    final SelectBuilder selectBuilder;
    final FromBuilder fromBuilder;
    final WhereBuilder whereBuilder;
    final OrderByBuilder orderByBuilder;
    final GroupByBuilder groupByBuilder;
    final HavingBuilder havingBuilder;
    final FieldExpressionResolverImpl selectFieldExpressionResolver;
    final FieldExpressionResolverImpl fieldExpressionResolver;
    final EntityMappingHelper entityMappingHelper;
    final SqlDB sqlDB;
    final QueryExecutor.Pagination pagination;

    public QueryBuilderImpl(EntityMappingHelper entityMappingHelper, SqlDB sqlDB, QueryExecutor.Pagination pagination) {
        Objects.requireNonNull(entityMappingHelper);
        Objects.requireNonNull(sqlDB);
        this.entityMappingHelper = entityMappingHelper;
        selectBuilder = new SelectBuilderImpl();
        fromBuilder = new FromBuilderImpl();
        whereBuilder = new WhereBuilderImpl();
        orderByBuilder = new OrderByBuilderImpl();
        groupByBuilder = new GroupByBuilderImpl();
        havingBuilder = new HavingBuilderImpl();
        this.selectFieldExpressionResolver = new FieldExpressionResolverImpl(new LinkedHashMap<>());
        this.fieldExpressionResolver = new FieldExpressionResolverImpl(new LinkedHashMap<>());
        this.sqlDB = sqlDB;
        this.pagination = (pagination == null) ? null : pagination;
    }

    @Override
    public FieldExpressionHolderFunc select(String fieldExpression) {
        return select(new FieldExpressionImpl(fieldExpression));
    }

    @Override
    public FieldExpressionHolderFunc field(String fieldExpression) {
        return field(new FieldExpressionImpl(fieldExpression));
    }

    @Override
    public FieldExpressionHolderFunc select(final FieldExpression fieldExpression) {

        if (selectFieldExpressionResolver.containsKey(fieldExpression)) {
            throw new FieldExpressionResolverException("Same fieldExpression '" + fieldExpression + "' more than once is not supported in selections");
        }

        return new FieldExpressionHolderFuncImpl(
            fieldExpression,
            selectFieldExpressionResolver.addKey(fieldExpression)
        );
    }

    @Override
    public FieldExpressionHolderFunc field(FieldExpression fieldExpression) {

        return new FieldExpressionHolderFuncImpl(
            fieldExpression,
            fieldExpressionResolver.addKey(fieldExpression)
        );
    }

    @Override
    public SelectBuilder selectBuilder() {
        return selectBuilder;
    }

    @Override
    public FromBuilder fromBuilder() {
        return fromBuilder;
    }

    @Override
    public WhereBuilder whereBuilder() {
        return whereBuilder;
    }

    @Override
    public OrderByBuilder orderByBuilder() {
        return orderByBuilder;
    }

    @Override
    public GroupByBuilder groupByBuilder() {
        return groupByBuilder;
    }

    @Override
    public HavingBuilder havingBuilder() {
        return havingBuilder;
    }

    @Override
    public Query build() {
        return new QueryImpl(
            fromBuilder.rootEntity(),
            fromBuilder.rootAlias(),
            selectFieldExpressionResolver,
            fieldExpressionResolver,
            selectBuilder.build(),
            fromBuilder.build(),
            whereBuilder.build(),
            orderByBuilder.build(),
            groupByBuilder.build(),
            havingBuilder.build(),
            pagination,
            entityMappingHelper,
            sqlDB
        );
    }
}
