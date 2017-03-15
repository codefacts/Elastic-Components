package elasta.orm.query;

import java.util.Objects;

/**
 * Created by Jango on 17/02/09.
 */
public class OrderByData {
    final String alias;
    final String column;
    final Order order;

    public OrderByData(String alias, String column, Order order) {
        Objects.requireNonNull(alias);
        Objects.requireNonNull(column);
        Objects.requireNonNull(order);
        this.alias = alias;
        this.column = column;
        this.order = order;
    }

    public String getAlias() {
        return alias;
    }

    public String getColumn() {
        return column;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderByData that = (OrderByData) o;

        if (alias != null ? !alias.equals(that.alias) : that.alias != null) return false;
        if (column != null ? !column.equals(that.column) : that.column != null) return false;
        return order == that.order;

    }

    @Override
    public int hashCode() {
        int result = alias != null ? alias.hashCode() : 0;
        result = 31 * result + (column != null ? column.hashCode() : 0);
        result = 31 * result + (order != null ? order.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OrderByData{" +
            "alias='" + alias + '\'' +
            ", column='" + column + '\'' +
            ", order=" + order +
            '}';
    }
}
