package elasta.orm.event;

import elasta.core.promise.intfs.Promise;
import elasta.pipeline.jsonwalker.JsonPath;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 3/28/2017.
 */
public interface EventHandler {

    Promise<JsonObject> handle(Params params);

    @Value
    @Builder
    final class Params {
        final OperationType operationType;
        final JsonObject entity;
        final JsonPath path;
        final JsonObject root;

        public Params(OperationType operationType, JsonObject entity, JsonPath path, JsonObject root) {
            Objects.requireNonNull(operationType);
            Objects.requireNonNull(entity);
            Objects.requireNonNull(path);
            Objects.requireNonNull(root);
            this.operationType = operationType;
            this.entity = entity;
            this.path = path;
            this.root = root;
        }
    }
}
