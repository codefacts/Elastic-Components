package elasta.orm.query.builder;

import elasta.orm.query.FieldExpression;
import elasta.orm.query.Order;import elasta.orm.query.Order;

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
