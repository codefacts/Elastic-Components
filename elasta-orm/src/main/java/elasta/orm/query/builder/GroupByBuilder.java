package elasta.orm.query.builder;

import elasta.orm.query.FieldExpression;import elasta.orm.query.FieldExpression;

import java.util.List;

/**
 * Created by Jango on 17/02/09.
 */
public interface GroupByBuilder {

    GroupByBuilder add(FieldExpression fieldExpression);

    GroupByBuilder add(List<FieldExpression> fieldExpressions);

    List<FieldExpression> build();

}
