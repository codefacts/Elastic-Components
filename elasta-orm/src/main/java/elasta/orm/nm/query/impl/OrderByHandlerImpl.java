package elasta.orm.nm.query.impl;

import elasta.orm.nm.query.Cqr;
import elasta.orm.nm.query.OrderByData;
import elasta.orm.nm.query.OrderByHandler;

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
