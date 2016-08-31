package elasta.composer.transformation;

import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by shahadat on 3/1/16.
 */
public class JsonTransformationPipeline implements Transform<JsonObject, JsonObject> {
    private final List<Transform<JsonObject, JsonObject>> transformList;

    public JsonTransformationPipeline(List<Transform<JsonObject, JsonObject>> transformList) {
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
