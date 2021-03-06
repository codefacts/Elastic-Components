package elasta.pipeline.validator.impl.json.object;

import com.google.common.collect.ImmutableList;
import elasta.pipeline.util.ErrorCodes;
import elasta.pipeline.validator.JsonObjectValidator;
import elasta.pipeline.validator.ValidationResult;
import elasta.pipeline.validator.ValidationResultBuilder;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by shahadat on 3/1/16.
 */
public class MinFieldCountValidator implements JsonObjectValidator {
    private final int minFieldCount;

    public MinFieldCountValidator(int minFieldCount) {
        this.minFieldCount = minFieldCount;
    }

    @Override
    public List<ValidationResult> validate(JsonObject json) {

        if (json.size() < minFieldCount) {
            return ImmutableList.of(invalidate(json));
        }

        return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setErrorCode(ErrorCodes.LENGTH_VALIDATION_ERROR.code())
            .setAdditionals(
                new JsonObject()
                    .put("minFieldCount", minFieldCount))
            .createValidationResult();
    }
}
