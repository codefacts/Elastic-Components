package elasta.orm.jpa;

import elasta.core.promise.intfs.Promise;
import elasta.orm.jpa.models.PropInfo;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;

/**
 * Created by Shahadat on 10/6/2016.
 */
public class IU {
    private final Map<String, PropInfo> propInfoMap;

    public IU(Map<String, PropInfo> propInfoMap) {
        this.propInfoMap = propInfoMap;
    }

    public Promise<List<UpdateInfo>> updateInfos(String model, JsonObject data) {
        return null;
    }
}
