package elasta.orm.nm.query.builder.impl;

import elasta.orm.nm.entitymodel.EntityMappingHelper;
import elasta.orm.nm.query.Query;
import elasta.orm.nm.query.QueryImpl;
import elasta.orm.nm.query.builder.*;
import elasta.orm.nm.query.impl.FieldExpressionImpl;

import java.util.HashMap;
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

    public QueryBuilderImpl(EntityMappingHelper entityMappingHelper) {
        Objects.requireNonNull(entityMappingHelper);
        this.entityMappingHelper = entityMappingHelper;
        selectBuilder = new SelectBuilderImpl();
        fromBuilder = new FromBuilderImpl();
        whereBuilder = new WhereBuilderImpl();
        orderByBuilder = new OrderByBuilderImpl();
        groupByBuilder = new GroupByBuilderImpl();
        havingBuilder = new HavingBuilderImpl();
        this.selectFieldExpressionResolver = new FieldExpressionResolverImpl(new LinkedHashMap<>());
        this.fieldExpressionResolver = new FieldExpressionResolverImpl(new LinkedHashMap<>());
    }

    @Override
    public FieldExpressionHolderFunc select(String fieldExpression) {

        FieldExpressionImpl expression = new FieldExpressionImpl(fieldExpression);

        return new FieldExpressionHolderFuncImpl(
            expression,
            selectFieldExpressionResolver.addKey(expression)
        );
    }

    @Override
    public FieldExpressionHolderFunc field(String fieldExpression) {

        FieldExpressionImpl expression = new FieldExpressionImpl(fieldExpression);

        return new FieldExpressionHolderFuncImpl(
            expression,
            fieldExpressionResolver.addKey(expression)
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
            entityMappingHelper);
    }
}
