package elasta.orm.query.builder.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.query.FieldExpression;
import elasta.orm.query.Order;
import elasta.orm.query.builder.FieldExpressionAndOrderPair;
import elasta.orm.query.builder.OrderByBuilder;import elasta.orm.query.Order;import elasta.orm.query.builder.FieldExpressionAndOrderPair;import elasta.orm.query.builder.OrderByBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jango on 17/02/09.
 */
final public class OrderByBuilderImpl implements OrderByBuilder {
    final List<FieldExpressionAndOrderPair> pairListBuilder = new ArrayList<>();

    @Override
    public OrderByBuilder add(FieldExpression fieldExpression, Order order) {
        pairListBuilder.add(new FieldExpressionAndOrderPair(fieldExpression, order));
        return this;
    }

    @Override
    public OrderByBuilder add(FieldExpressionAndOrderPair pair) {
        pairListBuilder.add(pair);
        return this;
    }

    @Override
    public OrderByBuilder add(List<FieldExpressionAndOrderPair> pairs) {
        pairListBuilder.addAll(pairs);
        return this;
    }

    @Override
    public List<FieldExpressionAndOrderPair> build() {
        return ImmutableList.copyOf(pairListBuilder);
    }

    public List<FieldExpressionAndOrderPair> getPairListBuilder() {
        return pairListBuilder;
    }
}
