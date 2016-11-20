package elasta.composer.pipeline.transformation.impl.json.object;

import elasta.composer.pipeline.transformation.JoJoTransformation;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Set;

/**
 * Created by shahadat on 3/5/16.
 */
public class TrimStringsTransformation implements JoJoTransformation {
    private final RecursiveMerge recursiveMerge;

    public TrimStringsTransformation() {
        this(null, null);
    }

    public TrimStringsTransformation(Set<List<String>> includes, Set<List<String>> excludes) {
        this.recursiveMerge = new RecursiveMerge(includes, excludes,
            val -> val instanceof String,
            (e, remove) -> e.setValue(e.getValue().toString().trim()),
            val -> false, (val, rem) -> {
        });
    }

    @Override
    public JsonObject transform(JsonObject json) {
        JsonObject transform = recursiveMerge.transform(json);
        return transform;
    }
}
