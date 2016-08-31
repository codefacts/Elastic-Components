package elasta.composer.transformation.impl.json.object;

import com.google.common.collect.ImmutableMap;
import elasta.composer.transformation.Transform;
import elasta.composer.util.Util;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by shahadat on 3/5/16.
 */
public class IntelligentConverterTransformation implements Transform<JsonObject, JsonObject> {
    final Map<String, Function<String, ?>> converters;

    public IntelligentConverterTransformation(Map<String, Function<String, ?>> converters) {
        Objects.requireNonNull(converters);
        this.converters = ImmutableMap.copyOf(converters);
    }

    @Override
    public JsonObject transform(JsonObject json) {

        if (json == null) return null;

        json.getMap().keySet().forEach(key -> {
            if (converters.get(key) != null) {
                json.put(key, converters.get(key).apply(json.getValue(key).toString()));
            } else {
                String[] split = Util.parseCamelOrSnake(key).toLowerCase().split("\\s");
                if (longType(split)) {
                    json.put(key, Long.parseLong(json.getValue(key).toString()));
                } else if (bool(split)) {
                    json.put(key, Boolean.parseBoolean(json.getValue(key).toString()));
                }
            }
        });
        return json;
    }

    private boolean dateTime(String[] key) {
        return key[key.length - 1].equals("date") || key[key.length - 1].equals("time")
            || key[key.length - 1].equals("timestamp");
    }

    private boolean bool(String[] key) {
        return key[0].equals("is");
    }

    private boolean longType(String[] key) {
        return key[key.length - 1].equals("id") || key[key.length - 1].equals("count");
    }
}
