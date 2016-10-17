package elasta.orm.jpa;

import elasta.orm.jpa.models.ModelInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by Jango on 10/9/2016.
 */
public class ModelInfoProviderImpl implements ModelInfoProvider {
    private final Map<String, ModelInfo> modelInfoByModelMap;

    public ModelInfoProviderImpl(Map<String, ModelInfo> modelInfoByModelMap) {
        this.modelInfoByModelMap = modelInfoByModelMap;
    }

    @Override
    public ModelInfo get(String model) {
        return modelInfoByModelMap.get(model);
    }

    @Override
    public String primaryKey(String model) {
        return modelInfoByModelMap.get(model).getPrimaryKey();
    }

    @Override
    public String primaryKey(String model, List<String> path) {

        ModelInfo modelInfo = get(model);

        for (String part : path) {
            modelInfo = get(modelInfo.getPropInfoMap().get(part).getRelationInfo().getJoinModelInfo().getChildModel());
        }
        return modelInfo.getPrimaryKey();
    }
}
