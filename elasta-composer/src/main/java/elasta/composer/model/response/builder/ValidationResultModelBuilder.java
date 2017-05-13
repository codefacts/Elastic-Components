package elasta.composer.model.response.builder;

import elasta.pipeline.validator.ValidationResult;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 5/14/2017.
 */
public interface ValidationResultModelBuilder {

    JsonObject build(BuildParams params);

    @Value
    @Builder
    final class BuildParams {
        final ValidationResult validationResult;

        BuildParams(ValidationResult validationResult) {
            Objects.requireNonNull(validationResult);
            this.validationResult = validationResult;
        }
    }
}
