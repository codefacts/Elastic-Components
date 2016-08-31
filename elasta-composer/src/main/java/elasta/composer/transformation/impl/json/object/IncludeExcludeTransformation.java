package elasta.composer.transformation.impl.json.object;

import com.google.common.collect.ImmutableSet;
import io.crm.transformation.Transform;
import io.vertx.core.json.JsonObject;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by shahadat on 3/5/16.
 */
public class IncludeExcludeTransformation implements Transform<JsonObject, JsonObject> {
    private final Set<String> includes;
    private final Set<String> excludes;

    public IncludeExcludeTransformation(Set<String> includes, Set<String> excludes) {
        this.includes = includes == null ? null : ImmutableSet.copyOf(includes);
        this.excludes = excludes == null ? null : ImmutableSet.copyOf(excludes);
    }

    @Override
    public JsonObject transform(JsonObject json) {
        if (json == null) return null;

        Stream<String> stream = json.getMap().keySet().stream();
        if (includes != null) {
            stream = stream.filter(k -> includes.contains(k));
        }
        if (excludes != null && excludes.size() > 0) {
            stream = stream.filter(k -> !excludes.contains(k));
        }
        JsonObject reduce = stream.reduce(new JsonObject(),
            (jsonObject, key) -> jsonObject.put(key, json.getValue(key)),
            (jsonObject1, jsonObject2) -> jsonObject1);
        return reduce;
    }
}
