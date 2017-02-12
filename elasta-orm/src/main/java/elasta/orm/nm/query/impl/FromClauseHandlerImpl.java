package elasta.orm.nm.query.impl;

import elasta.orm.nm.query.FromClauseHandler;
import elasta.orm.nm.query.JoinClauseHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Jango on 17/02/09.
 */
final public class FromClauseHandlerImpl implements FromClauseHandler {
    final List<JoinClauseHandler> joinClauseHandlers;

    public FromClauseHandlerImpl(List<JoinClauseHandler> joinClauseHandlers) {
        Objects.requireNonNull(joinClauseHandlers);
        this.joinClauseHandlers = joinClauseHandlers;
    }

    @Override
    public String toSql() {
        return joinClauseHandlers.stream()
            .map(JoinClauseHandler::toSql).collect(Collectors.joining(","));
    }
}
