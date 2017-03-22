package elasta.orm.query.expression.impl;

import elasta.orm.query.expression.core.ColumnAliasPair;
import elasta.orm.query.expression.Cqr;
import elasta.orm.query.expression.GroupByHandler;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Jango on 17/02/09.
 */
final public class GroupByHandlerImpl implements GroupByHandler {
    final List<ColumnAliasPair> columnAliasPairs;

    public GroupByHandlerImpl(List<ColumnAliasPair> columnAliasPairs) {
        Objects.requireNonNull(columnAliasPairs);
        this.columnAliasPairs = columnAliasPairs;
    }

    @Override
    public String toSql() {
        return columnAliasPairs.stream()
            .map(columnAliasPair -> columnAliasPair.getAlias() + Cqr.PERIOD + columnAliasPair.getColumn())
            .collect(Collectors.joining(Cqr.COMMA));
    }
}
