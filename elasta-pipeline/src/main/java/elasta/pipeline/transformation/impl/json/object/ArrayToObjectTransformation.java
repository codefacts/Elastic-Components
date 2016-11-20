package elasta.pipeline.transformation.impl.json.object;

import elasta.pipeline.transformation.Transformation;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by shahadat on 3/5/16.
 */
public class ArrayToObjectTransformation implements Transformation<JsonArray, JsonObject> {

    @Override
    public JsonObject transform(JsonArray array) {

        if (array == null) return null;

        JsonObject jsonObject = new JsonObject();
        for (int i = 0; i < array.size(); i++) {
            JsonObject json = array.getJsonObject(i);
            if (json == null) {
                continue;
            }
            jsonObject.getMap().putAll(json.getMap());
        }
        return jsonObject;
    }
}
