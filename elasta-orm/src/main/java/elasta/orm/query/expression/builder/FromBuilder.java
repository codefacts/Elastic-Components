package elasta.orm.query.expression.builder;

import elasta.orm.query.expression.PathExpression;import elasta.orm.query.expression.builder.impl.PathExpressionAndAliasPair;import elasta.sql.core.JoinType;

import java.util.List;
import java.util.Optional;

/**
 * Created by Jango on 17/02/09.
 */
public interface FromBuilder {

    FromBuilder root(String entity, String alias);

    FromBuilder join(PathExpression collectionPath, String collectionAlias, Optional<JoinType> joinType);

    String rootEntity();

    String rootAlias();

    List<PathExpressionAndAliasPair> build();
}
