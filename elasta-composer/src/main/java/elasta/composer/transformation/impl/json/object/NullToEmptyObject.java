package elasta.composer.transformation.impl.json.object;

import io.crm.transformation.Transform;
import io.vertx.core.json.JsonObject;

/**
 * Created by shahadat on 3/10/16.
 */
public class NullToEmptyObject implements Transform<JsonObject, JsonObject> {
    @Override
    public JsonObject transform(JsonObject val) {
        return val == null ? new JsonObject() : val;
    }
}
