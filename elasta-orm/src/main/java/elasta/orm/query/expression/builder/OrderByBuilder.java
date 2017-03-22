package elasta.orm.query.expression.builder;

import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.core.Order;

import java.util.List;

/**
 * Created by Jango on 17/02/09.
 */
public interface OrderByBuilder {

    OrderByBuilder add(FieldExpression fieldExpression, Order order);

    OrderByBuilder add(FieldExpressionAndOrderPair pair);

    OrderByBuilder add(List<FieldExpressionAndOrderPair> pairs);

    List<FieldExpressionAndOrderPair> build();
}
