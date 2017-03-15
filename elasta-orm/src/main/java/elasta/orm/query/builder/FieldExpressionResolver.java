package elasta.orm.query.builder;

import elasta.criteria.ParamsBuilder;
import elasta.orm.query.FieldExpression;

/**
 * Created by Jango on 17/02/09.
 */
public interface FieldExpressionResolver {

    String resolve(FieldExpression fieldExpression, ParamsBuilder paramsBuilder);
}
