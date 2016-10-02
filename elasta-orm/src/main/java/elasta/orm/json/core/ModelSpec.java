package elasta.orm.json.core;

import java.util.List;

/**
 * Created by Jango on 10/2/2016.
 */
public class ModelSpec {
    private final String name;
    private final String table;
    private final String primaryKey;
    private final List<ModelProp> modelProps;

    public ModelSpec(String name, String table, String primaryKey, List<ModelProp> modelProps) {
        this.name = name;
        this.table = table;
        this.primaryKey = primaryKey;
        this.modelProps = modelProps;
    }

    public String getName() {
        return name;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public List<ModelProp> getModelProps() {
        return modelProps;
    }
}
