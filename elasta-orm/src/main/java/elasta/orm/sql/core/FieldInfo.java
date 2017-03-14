package elasta.orm.sql.core;

import java.util.List;

/**
 * Created by Jango on 9/22/2016.
 */
final public class FieldInfo {
    private final String path;
    private final List<String> fields;

    public FieldInfo(String path, List<String> fields) {
        this.path = path;
        this.fields = fields;
    }

    public String getPath() {
        return path;
    }

    public List<String> getFields() {
        return fields;
    }
}
