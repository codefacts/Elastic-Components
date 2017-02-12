package elasta.orm.nm.query;

/**
 * Created by Jango on 17/02/09.
 */
public interface FieldExpression {

    PathExpression getParentPath();

    String getField();

    PathExpression toPathExpression();
}
