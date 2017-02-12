package elasta.orm.nm.query.builder;

import elasta.orm.nm.criteria.ParamsBuilder;
import elasta.orm.nm.query.FieldExpression;

import java.util.Map;

/**
 * Created by Jango on 17/02/09.
 */
public interface FieldExpressionResolver {

    String resolve(FieldExpression fieldExpression, ParamsBuilder paramsBuilder);
}
