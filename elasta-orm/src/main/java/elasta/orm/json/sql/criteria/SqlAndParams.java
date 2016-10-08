package elasta.orm.json.sql.criteria;

import io.vertx.core.json.JsonArray;

/**
 * Created by Jango on 10/8/2016.
 */
public class SqlAndParams {
    private final String sql;
    private final JsonArray params;

    public SqlAndParams(String sql, JsonArray params) {
        this.sql = sql;
        this.params = params;
    }

    public String getSql() {
        return sql;
    }

    public JsonArray getParams() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SqlAndParams that = (SqlAndParams) o;

        if (sql != null ? !sql.equals(that.sql) : that.sql != null) return false;
        return params != null ? params.equals(that.params) : that.params == null;

    }

    @Override
    public int hashCode() {
        int result = sql != null ? sql.hashCode() : 0;
        result = 31 * result + (params != null ? params.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SqlAndParams{" +
            "sql='" + sql + '\'' +
            ", params=" + params +
            '}';
    }
}
