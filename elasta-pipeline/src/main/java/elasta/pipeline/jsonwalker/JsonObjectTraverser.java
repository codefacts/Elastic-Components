package elasta.pipeline.jsonwalker;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 3/26/2017.
 */
public interface JsonObjectTraverser {
    Promise<JsonObject> traverse(Params params);

    @Value
    @Builder
    final class Params {
        final JsonObject jsonObject;
        final JsonObjectTraverseFunction jsonObjectTraverseFunction;
        final JsonObjectTraverseFilter jsonObjectTraverseFilter;

        public Params(JsonObject jsonObject, JsonObjectTraverseFunction jsonObjectTraverseFunction, JsonObjectTraverseFilter jsonObjectTraverseFilter) {
            Objects.requireNonNull(jsonObject);
            Objects.requireNonNull(jsonObjectTraverseFunction);
            Objects.requireNonNull(jsonObjectTraverseFilter);
            this.jsonObject = jsonObject;
            this.jsonObjectTraverseFunction = jsonObjectTraverseFunction;
            this.jsonObjectTraverseFilter = jsonObjectTraverseFilter;
        }
    }
}
