package elasta.composer.transformation.impl.json.object;

import elasta.composer.transformation.Transform;
import elasta.composer.util.Util;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by shahadat on 3/1/16.
 */
public class DefaultValueTransformation implements Transform<JsonObject, JsonObject> {
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

            Object jsonValue = json.getValue(key);

            if (jsonValue == null) {
                json.put(key, emptyfy(defVal));
            } else if (jsonValue instanceof JsonObject) {
                recursive((JsonObject) jsonValue, Util.as(defVal, JsonObject.class));
            }
        });
        return json;
    }

    private Object emptyfy(Object val) {

        Objects.requireNonNull(val);

        if (val instanceof JsonObject) {
            return new JsonObject();
        } else if (val instanceof JsonArray) {
            return new JsonArray();
        } else {
            return val;
        }
    }
}
