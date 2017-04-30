package elasta.orm.query.expression;

/**
 * Created by Jango on 17/02/09.
 */
public interface FieldExpression {

    PathExpression getParent();

    String getField();

    PathExpression toPathExpression();

    int size();
}
