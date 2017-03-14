package elasta.orm.nm.query.builder;

import elasta.orm.sql.sql.core.JoinType;
import elasta.orm.nm.query.PathExpression;
import elasta.orm.nm.query.builder.impl.PathExpressionAndAliasPair;

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
