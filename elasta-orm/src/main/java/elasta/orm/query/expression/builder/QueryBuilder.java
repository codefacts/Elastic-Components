package elasta.orm.query.expression.builder;

import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.Query;

/**
 * Created by Jango on 17/02/09.
 */
public interface QueryBuilder {

    FieldExpressionHolderFunc select(String fieldExpression);

    FieldExpressionHolderFunc field(String fieldExpression);

    FieldExpressionHolderFunc select(FieldExpression fieldExpression);

    FieldExpressionHolderFunc field(FieldExpression fieldExpression);

    SelectBuilder selectBuilder();

    FromBuilder fromBuilder();

    WhereBuilder whereBuilder();

    OrderByBuilder orderByBuilder();

    GroupByBuilder groupByBuilder();

    HavingBuilder havingBuilder();

    Query build();
}
