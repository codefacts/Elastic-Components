package elasta.orm.nm.query.builder;

import elasta.orm.nm.query.FieldExpression;
import elasta.orm.nm.query.Query;

/**
 * Created by Jango on 17/02/09.
 */
public interface QueryBuilder {

    FieldExpressionHolderFunc select(String fieldExpression);

    FieldExpressionHolderFunc field(String expressionString);

    SelectBuilder selectBuilder();

    FromBuilder fromBuilder();

    WhereBuilder whereBuilder();

    OrderByBuilder orderByBuilder();

    GroupByBuilder groupByBuilder();

    HavingBuilder havingBuilder();

    Query build();
}
