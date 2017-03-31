package elasta.pipeline.jsonwalker;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 3/26/2017.
 */
public interface JsonObjectTraverseFunction {

    Promise<JsonObject> traverse(Params params);

    @Value
    @Builder
    final class Params {
        final JsonObject jsonObject;
        final JsonPath path;
        final JsonObject root;

        public Params(JsonObject jsonObject, JsonPath path, JsonObject root) {
            Objects.requireNonNull(jsonObject);
            Objects.requireNonNull(path);
            Objects.requireNonNull(root);
            this.jsonObject = jsonObject;
            this.path = path;
            this.root = root;
        }
    }

    static void main(String[] asdf) {
        Params.builder().build();
    }
}
