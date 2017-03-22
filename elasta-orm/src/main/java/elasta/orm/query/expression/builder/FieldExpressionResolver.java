package elasta.orm.query.expression.builder;

import elasta.criteria.ParamsBuilder;
import elasta.orm.query.expression.FieldExpression;

/**
 * Created by Jango on 17/02/09.
 */
public interface FieldExpressionResolver {

    String resolve(FieldExpression fieldExpression, ParamsBuilder paramsBuilder);
}
