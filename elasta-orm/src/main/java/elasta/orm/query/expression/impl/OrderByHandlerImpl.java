package elasta.orm.query.expression.impl;

import elasta.orm.query.expression.Cqr;
import elasta.orm.query.expression.core.OrderByData;
import elasta.orm.query.expression.OrderByHandler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Jango on 17/02/09.
 */
final public class OrderByHandlerImpl implements OrderByHandler {
    final List<OrderByData> orderByDatas;

    public OrderByHandlerImpl(List<OrderByData> orderByDatas) {
        Objects.requireNonNull(orderByDatas);
        this.orderByDatas = orderByDatas;
    }

    @Override
    public String toSql() {
        return orderByDatas.stream()
            .map(orderByData -> orderByData.getAlias() + "." + orderByData.getColumn() + " " + orderByData.getOrder())
            .collect(Collectors.joining(Cqr.COMMA));
    }
}
