package elasta.orm.json.sql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 10/8/2016.
 */
public class UpdateTpl {
    private final String table;
    private final JsonObject data;
    private final String where;
    private final JsonArray jsonArray;

    public UpdateTpl(String table, JsonObject data, String where, JsonArray jsonArray) {
        this.table = table;
        this.data = data;
        this.where = where;
        this.jsonArray = jsonArray;
    }
}
