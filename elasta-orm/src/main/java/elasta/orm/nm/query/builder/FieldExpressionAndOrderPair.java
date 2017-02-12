package elasta.orm.nm.query.builder;

import elasta.orm.nm.query.FieldExpression;
import elasta.orm.nm.query.Order;

import java.util.Objects;

/**
 * Created by Jango on 17/02/09.
 */
final public class FieldExpressionAndOrderPair {
    final FieldExpression fieldExpression;
    final Order order;

    public FieldExpressionAndOrderPair(FieldExpression fieldExpression, Order order) {
        Objects.requireNonNull(fieldExpression);
        Objects.requireNonNull(order);
        this.fieldExpression = fieldExpression;
        this.order = order;
    }

    public FieldExpression getFieldExpression() {
        return fieldExpression;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "FieldExpressionAndOrderPair{" +
            "fieldExpression=" + fieldExpression +
            ", order=" + order +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldExpressionAndOrderPair that = (FieldExpressionAndOrderPair) o;

        if (fieldExpression != null ? !fieldExpression.equals(that.fieldExpression) : that.fieldExpression != null)
            return false;
        return order == that.order;

    }

    @Override
    public int hashCode() {
        int result = fieldExpression != null ? fieldExpression.hashCode() : 0;
        result = 31 * result + (order != null ? order.hashCode() : 0);
        return result;
    }
}
