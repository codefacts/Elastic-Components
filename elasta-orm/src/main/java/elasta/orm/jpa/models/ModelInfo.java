package elasta.orm.jpa;

import java.util.Map;

/**
 * Created by Jango on 10/6/2016.
 */
public class ModelInfo {
    private final String name;
    private final String table;
    private final String primaryKey;
    private final Map<String, PropInfo> propInfoMap;

    public ModelInfo(String name, String table, String primaryKey, Map<String, PropInfo> propInfoMap) {
        this.name = name;
        this.table = table;
        this.primaryKey = primaryKey;
        this.propInfoMap = propInfoMap;
    }

    public String getName() {
        return name;
    }

    public String getTable() {
        return table;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public Map<String, PropInfo> getPropInfoMap() {
        return propInfoMap;
    }
}
