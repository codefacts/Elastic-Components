package elasta.orm.nm.query;

/**
 * Created by Jango on 17/02/09.
 */
public interface ColumnExpression {

    PathExpression getParentPath();

    String getParent();

    String getColumn();

    PathExpression toPathExpression();
}
