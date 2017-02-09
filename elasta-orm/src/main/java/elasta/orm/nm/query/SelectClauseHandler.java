package elasta.orm.nm.query;

import elasta.orm.nm.criteria.Func;
import elasta.orm.nm.criteria.ParamsBuilder;

/**
 * Created by Jango on 17/02/08.
 */
public interface SelectClauseHandler {
    String toSql();
}