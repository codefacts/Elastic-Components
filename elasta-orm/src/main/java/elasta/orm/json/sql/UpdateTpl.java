package elasta.orm.json.sql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 10/8/2016.
 */
public class UpdateTpl {
    private final UpdateOperationType updateOperationType;
    private final String table;
    private final JsonObject data;
    private final String where;
    private final JsonArray jsonArray;

    public UpdateTpl(UpdateOperationType updateOperationType, String table, JsonObject data, String where, JsonArray jsonArray) {
        this.updateOperationType = updateOperationType;
        this.table = table;
        this.data = data;
        this.where = where;
        this.jsonArray = jsonArray;
    }

    public UpdateOperationType getUpdateOperationType() {
        return updateOperationType;
    }

    public String getTable() {
        return table;
    }

    public JsonObject getData() {
        return data;
    }

    public String getWhere() {
        return where;
    }

    public JsonArray getJsonArray() {
        return jsonArray;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UpdateTpl updateTpl = (UpdateTpl) o;

        if (updateOperationType != updateTpl.updateOperationType) return false;
        if (table != null ? !table.equals(updateTpl.table) : updateTpl.table != null) return false;
        if (data != null ? !data.equals(updateTpl.data) : updateTpl.data != null) return false;
        if (where != null ? !where.equals(updateTpl.where) : updateTpl.where != null) return false;
        return jsonArray != null ? jsonArray.equals(updateTpl.jsonArray) : updateTpl.jsonArray == null;

    }

    @Override
    public int hashCode() {
        int result = updateOperationType != null ? updateOperationType.hashCode() : 0;
        result = 31 * result + (table != null ? table.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (where != null ? where.hashCode() : 0);
        result = 31 * result + (jsonArray != null ? jsonArray.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UpdateTpl{" +
            "updateOperationType=" + updateOperationType +
            ", dependentTable='" + table + '\'' +
            ", data=" + data +
            ", where='" + where + '\'' +
            ", jsonArray=" + jsonArray +
            '}';
    }
}
