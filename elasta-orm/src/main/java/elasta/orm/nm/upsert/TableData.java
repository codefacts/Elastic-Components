package elasta.orm.nm.upsert;

import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by Jango on 2017-01-09.
 */
final public class TableData {
    final String table;
    final String[] primaryColumns;
    final JsonObject values;

    public TableData(String table, String[] primaryColumns, JsonObject values) {
        this.table = table;
        this.primaryColumns = primaryColumns;
        this.values = values;
    }

    public String getTable() {
        return table;
    }

    public String[] getPrimaryColumns() {
        return primaryColumns;
    }

    public JsonObject getValues() {
        return values;
    }
}
