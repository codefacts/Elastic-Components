package elasta.composer.pipeline.transformation.impl.json.object;

import com.google.common.collect.ImmutableList;
import elasta.composer.pipeline.transformation.Transformation;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by shahadat on 3/5/16.
 */
public class ObjectToArrayTransformation implements Transformation<JsonObject, JsonArray> {
    private final List<String> keyOrder;

    public ObjectToArrayTransformation(List<String> keyOrder) {
        this.keyOrder = ImmutableList.copyOf(keyOrder);
    }

    @Override
    public JsonArray transform(JsonObject jo) {

        if (jo == null) return null;

        JsonArray objects = new JsonArray();
        if (keyOrder != null) {
            keyOrder.forEach(key -> objects.add(new JsonObject().put(key, jo.getValue(key))));
        } else {
            jo.forEach(e -> objects.add(new JsonObject().put(e.getKey(), e.getValue())));
        }
        return objects;
    }
}
