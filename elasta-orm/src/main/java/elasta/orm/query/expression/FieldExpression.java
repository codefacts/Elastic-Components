package elasta.orm.query.expression;

/**
 * Created by Jango on 17/02/09.
 */
public interface FieldExpression {

    PathExpression getParentPath();

    String getField();

    PathExpression toPathExpression();
}
