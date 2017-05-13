package elasta.composer.model.response.builder;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 5/14/2017.
 */
public interface ValidationErrorModelBuilder {

    JsonObject build(BuildParams params);

    @Value
    @Builder
    final class BuildParams {
        final String entity;
        final List<JsonObject> validationErrors;

        BuildParams(String entity, List<JsonObject> validationErrors) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(validationErrors);
            this.entity = entity;
            this.validationErrors = validationErrors;
        }
    }
}
