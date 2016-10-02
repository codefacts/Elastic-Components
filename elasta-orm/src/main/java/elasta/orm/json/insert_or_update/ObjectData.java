package elasta.orm.json.insert_or_update;

import java.util.Map;

/**
 * Created by Jango on 9/25/2016.
 */
public class ObjectData {
    private final String table;
    private final String primaryKey;
    private final Map<String, Object> data;

    public ObjectData(String table, String primaryKey, Map<String, Object> data) {
        this.table = table;
        this.primaryKey = primaryKey;
        this.data = data;
    }

    public String getTable() {
        return table;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
