package elasta.orm.dataflow;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 3/26/2017.
 */
public interface JsonObjectTraverseFunction {

    JsonObject traverse(Params params);

    @Value
    @Builder
    final class Params {
        final JsonObject jsonObject;
        final JsonPath path;
        final JsonObject parent;
        final JsonObject root;

        public Params(JsonObject jsonObject, JsonPath path, JsonObject parent, JsonObject root) {
            Objects.requireNonNull(jsonObject);
            Objects.requireNonNull(path);
            Objects.requireNonNull(root);
            this.jsonObject = jsonObject;
            this.path = path;
            this.parent = (parent == null) ? null : parent;
            this.root = root;
        }

        Optional<JsonObject> getParent() {
            return Optional.ofNullable(parent);
        }
    }

    static void main(String[] asdf) {
        Params.builder().build();
    }
}
