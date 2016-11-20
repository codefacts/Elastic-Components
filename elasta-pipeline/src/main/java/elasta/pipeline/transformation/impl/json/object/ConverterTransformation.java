package elasta.pipeline.transformation.impl.json.object;

import elasta.pipeline.transformation.Transformation;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.function.Function;

/**
 * Created by shahadat on 3/5/16.
 */
public class ConverterTransformation implements Transformation<JsonObject, JsonObject> {
    final Map<String, Function<Object, Object>> converters;

    public ConverterTransformation(Map<String, Function<Object, Object>> converters) {
        this.converters = converters;
    }

    @Override
    public JsonObject transform(JsonObject json) {
        if (json == null) {
            return null;
        }
        converters.forEach((k, v) -> {
            Object value = json.getValue(k);
            if (value != null) {
                json.put(k, v.apply(value));
            }
        });
        return json;
    }
}
