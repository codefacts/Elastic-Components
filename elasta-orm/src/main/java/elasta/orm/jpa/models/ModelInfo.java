package elasta.orm.jpa.models;

import java.util.Map;

/**
 * Created by Jango on 10/6/2016.
 */
public class ModelInfo {
    private final String name;
    private final Map<String, PropInfo> propInfoMap;

    public ModelInfo(String name, Map<String, PropInfo> propInfoMap) {
        this.name = name;
        this.propInfoMap = propInfoMap;
    }
}
