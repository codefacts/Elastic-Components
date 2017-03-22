package elasta.orm.query.expression.impl;

import elasta.orm.query.expression.FromClauseHandler;
import elasta.orm.query.expression.JoinClauseHandler;

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
