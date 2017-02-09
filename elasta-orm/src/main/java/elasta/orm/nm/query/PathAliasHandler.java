package elasta.orm.nm.query;

/**
 * Created by Jango on 17/02/09.
 */
public interface PathAliasHandler {
    String toAliasedColumn(ColumnExpression columnExpression);
}
