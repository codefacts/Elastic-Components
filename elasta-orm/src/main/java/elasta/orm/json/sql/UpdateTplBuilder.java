package elasta.orm.json.sql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class UpdateTplBuilder {
    private String table;
    private JsonObject data;
    private String where;
    private JsonArray jsonArray;

    public UpdateTplBuilder setTable(String table) {
        this.table = table;
        return this;
    }

    public UpdateTplBuilder setData(JsonObject data) {
        this.data = data;
        return this;
    }

    public UpdateTplBuilder setWhere(String where) {
        this.where = where;
        return this;
    }

    public UpdateTplBuilder setJsonArray(JsonArray jsonArray) {
        this.jsonArray = jsonArray;
        return this;
    }

    public UpdateTpl createUpdateTpl() {
        return new UpdateTpl(table, data, where, jsonArray);
    }
}