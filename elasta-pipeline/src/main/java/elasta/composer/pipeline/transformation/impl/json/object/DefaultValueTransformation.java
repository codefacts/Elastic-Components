package elasta.composer.pipeline.transformation.impl.json.object;

import elasta.composer.pipeline.transformation.Transformation;
import elasta.composer.pipeline.util.Util;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by shahadat on 3/1/16.
 */
public class DefaultValueTransformation implements Transformation<JsonObject, JsonObject> {
    private final JsonObject defaultValue;

    public DefaultValueTransformation(JsonObject defaultValue) {
        Objects.requireNonNull(defaultValue, "DefaultValueTransformation: DefaultValue:JsonObject must not be null.");
        this.defaultValue = Util.toImmutable(defaultValue);
    }

    @Override
    public JsonObject transform(JsonObject json) {
        if (json == null) return new JsonObject();

        return recursive(json, defaultValue);
    }

    private JsonObject recursive(JsonObject json, JsonObject defJson) {

        defJson.getMap().forEach((key, defVal) -> {

            Object value = json.getValue(key);

            if (value == null) {
                json.put(key, defVal);
            } else if (value instanceof JsonObject) {
                recursive((JsonObject) value, Util.as(defVal));
            }
        });
        return json;
    }
}
