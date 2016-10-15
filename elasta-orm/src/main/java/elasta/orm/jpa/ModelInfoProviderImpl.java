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
    public ModelInfo get(String model, List<String> path) {
        return get(model, path, path.size());
    }

    @Override
    public ModelInfo get(final String model, final List<String> path, final int len) {
        ModelInfo modelInfo = modelInfoByModelMap.get(model);

        for (int i = 0; i < len; i++) {
            String field = path.get(i);

            modelInfo = modelInfoByModelMap.get(
                modelInfo.getPropInfoMap().get(field).getRelationInfo().getJoinModelInfo().getChildModel()
            );
        }

        return modelInfo;
    }

    @Override
    public String primaryKey(String model) {
        return modelInfoByModelMap.get(model).getPrimaryKey();
    }

    @Override
    public String primaryKey(String model, List<String> path) {
        return get(model, path).getPrimaryKey();
    }
}
