package elasta.pipeline.transformation.impl.json.object;

import elasta.pipeline.transformation.Transformation;
import io.vertx.core.json.JsonObject;

/**
 * Created by shahadat on 3/10/16.
 */
public class NullToEmptyObject implements Transformation<JsonObject, JsonObject> {
    @Override
    public JsonObject transform(JsonObject val) {
        return val == null ? new JsonObject() : val;
    }
}
