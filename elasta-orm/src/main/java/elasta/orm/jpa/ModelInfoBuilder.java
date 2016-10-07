package elasta.orm.jpa;

import java.util.Map;

public class ModelInfoBuilder {
    private String name;
    private String table;
    private String primaryKey;
    private Map<String, PropInfo> propInfoMap;

    public ModelInfoBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ModelInfoBuilder setTable(String table) {
        this.table = table;
        return this;
    }

    public ModelInfoBuilder setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }

    public ModelInfoBuilder setPropInfoMap(Map<String, PropInfo> propInfoMap) {
        this.propInfoMap = propInfoMap;
        return this;
    }

    public ModelInfo createModelInfo() {
        return new ModelInfo(name, table, primaryKey, propInfoMap);
    }
}