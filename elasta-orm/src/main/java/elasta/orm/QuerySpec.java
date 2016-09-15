package elasta.orm;

import java.util.List;

/**
 * Created by Jango on 9/15/2016.
 */
final public class QuerySpec {
    private final String sql;
    private final List<Object> parameters;

    public QuerySpec(String sql, List<Object> parameters) {
        this.sql = sql;
        this.parameters = parameters;
    }

    public String getSql() {
        return sql;
    }

    public List<Object> getParameters() {
        return parameters;
    }
}
