package elasta.composer.transformation;

import elasta.composer.transformation.impl.json.object.JoJoTransform;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by shahadat on 3/1/16.
 */
public class JsonTransformationPipeline implements JoJoTransform {
    private final List<JoJoTransform> transformList;

    public JsonTransformationPipeline(List<JoJoTransform> transformList) {
        this.transformList = transformList;
    }

    @Override
    public JsonObject transform(JsonObject val) {
        for (Transform<JsonObject, JsonObject> transform : transformList) {
            val = transform.transform(val);
        }
        return val;
    }
}
