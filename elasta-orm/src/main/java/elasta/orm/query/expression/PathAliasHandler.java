package elasta.orm.query.expression;

/**
 * Created by Jango on 17/02/09.
 */
public interface PathAliasHandler {
    String toAliasedColumn(FieldExpression fieldExpression);
}
