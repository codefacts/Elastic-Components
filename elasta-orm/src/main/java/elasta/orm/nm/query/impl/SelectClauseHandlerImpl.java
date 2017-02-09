package elasta.orm.nm.query.impl;

import elasta.orm.nm.criteria.Func;
import elasta.orm.nm.criteria.ParamsBuilder;
import elasta.orm.nm.query.SelectClauseHandler;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Jango on 17/02/08.
 */
final public class SelectClauseHandlerImpl implements SelectClauseHandler {
    static final String COMMA = ",";
    final Func[] funcs;
    final ParamsBuilder paramsBuilder;

    public SelectClauseHandlerImpl(Func[] funcs, ParamsBuilder paramsBuilder) {
        Objects.requireNonNull(funcs);
        Objects.requireNonNull(paramsBuilder);
        this.funcs = funcs;
        this.paramsBuilder = paramsBuilder;
    }

    @Override
    public String toSql() {
        return Arrays.asList(funcs).stream()
            .map(func -> func.get(paramsBuilder))
            .collect(Collectors.joining(COMMA));
    }
}
