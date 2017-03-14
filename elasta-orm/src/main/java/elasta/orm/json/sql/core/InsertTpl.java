package elasta.orm.json.sql.core;

import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 10/8/2016.
 */
public class InsertTpl {
    private final String table;
    private final JsonObject data;

    public InsertTpl(String table, JsonObject data) {
        this.table = table;
        this.data = data;
    }

    public String getTable() {
        return table;
    }

    public JsonObject getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InsertTpl insertTpl = (InsertTpl) o;

        if (table != null ? !table.equals(insertTpl.table) : insertTpl.table != null) return false;
        return data != null ? data.equals(insertTpl.data) : insertTpl.data == null;

    }

    @Override
    public int hashCode() {
        int result = table != null ? table.hashCode() : 0;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}
