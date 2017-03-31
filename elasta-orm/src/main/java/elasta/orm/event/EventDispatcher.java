package elasta.orm.event;

import elasta.core.promise.intfs.Promise;
import elasta.pipeline.jsonwalker.JsonPath;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 3/28/2017.
 */
public interface EventDispatcher {

    Promise<JsonObject> dispatch(EventDispatcher.Params params);

    @Value
    @Builder
    final class Params {
        final JsonObject entity;
        final JsonPath path;
        final JsonObject root;

        public Params(JsonObject entity, JsonPath path, JsonObject root) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(path);
            Objects.requireNonNull(root);
            this.entity = entity;
            this.path = path;
            this.root = root;
        }
    }
}
