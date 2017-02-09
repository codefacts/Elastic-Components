package elasta.orm.nm.query;

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
            .map(columnAliasPair -> columnAliasPair.getAlias() + C.PERIOD + columnAliasPair.getColumn())
            .collect(Collectors.joining(C.COMMA));
    }
}
