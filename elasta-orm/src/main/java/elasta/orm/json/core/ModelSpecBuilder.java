package elasta.orm.json.core;

import java.util.List;

public class ModelSpecBuilder {
    private String name;
    private String table;
    private String primaryKey;
    private List<ModelProp> modelProps;

    public ModelSpecBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ModelSpecBuilder setTable(String table) {
        this.table = table;
        return this;
    }

    public ModelSpecBuilder setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }

    public ModelSpecBuilder setModelProps(List<ModelProp> modelProps) {
        this.modelProps = modelProps;
        return this;
    }

    public ModelSpec createModelSpec() {
        return new ModelSpec(name, table, primaryKey, modelProps);
    }
}