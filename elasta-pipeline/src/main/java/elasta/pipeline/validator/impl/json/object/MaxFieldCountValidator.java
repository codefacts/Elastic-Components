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
public class MaxFieldCountValidator implements JsonObjectValidator {
    private final int maxFieldCount;

    public MaxFieldCountValidator(int maxFieldCount) {
        this.maxFieldCount = maxFieldCount;
    }

    @Override
    public List<ValidationResult> validate(JsonObject json) {

        if (json.size() > maxFieldCount) {
            return ImmutableList.of(invalidate(json));
        }

        return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setErrorCode(ErrorCodes.LENGTH_VALIDATION_ERROR.code())
            .setAdditionals(
                new JsonObject()
                    .put("maxFieldCount", maxFieldCount))
            .createValidationResult();
    }
}
