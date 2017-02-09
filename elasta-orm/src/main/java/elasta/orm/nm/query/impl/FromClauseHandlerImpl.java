package elasta.orm.nm.query.impl;

import elasta.orm.nm.query.FromClauseHandler;
import elasta.orm.nm.query.JoinClauseHandler;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Jango on 17/02/09.
 */
final public class FromClauseHandlerImpl implements FromClauseHandler {
    final JoinClauseHandler[] joinClauseHandlers;

    public FromClauseHandlerImpl(JoinClauseHandler[] joinClauseHandlers) {
        Objects.requireNonNull(joinClauseHandlers);
        this.joinClauseHandlers = joinClauseHandlers;
    }

    @Override
    public String toSql() {
        return Arrays.asList(joinClauseHandlers).stream()
            .map(JoinClauseHandler::toSql).collect(Collectors.joining(","));
    }
}
