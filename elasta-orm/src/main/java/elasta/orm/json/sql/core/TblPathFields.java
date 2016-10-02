package elasta.orm.json.sql.core;

import java.util.List;

/**
 * Created by Jango on 10/2/2016.
 */
public class TblPathFields {
    private final String table;
    private final String alias;
    private final List<String> path;
    private final List<String> fields;

    public TblPathFields(String table, String alias, List<String> path, List<String> fields) {
        this.table = table;
        this.alias = alias;
        this.path = path;
        this.fields = fields;
    }

    public String getTable() {
        return table;
    }

    public String getAlias() {
        return alias;
    }

    public List<String> getPath() {
        return path;
    }

    public List<String> getFields() {
        return fields;
    }
}
