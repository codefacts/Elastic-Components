package elasta.composer.pipeline.validator.impl;

import elasta.composer.pipeline.util.ErrorCodes;
import elasta.composer.pipeline.validator.JsonObjectValidator;
import elasta.composer.pipeline.validator.ValidationResult;
import elasta.composer.pipeline.validator.ValidationResultBuilder;
import io.vertx.core.json.JsonObject;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Created by shahadat on 2/28/16.
 */
public class RangeValidator implements JsonObjectValidator {
    private final String field;
    private final long min, max;

    public RangeValidator(String field, long min, long max) {
        this.min = min;
        this.max = max;
        requireNonNull(field);
        this.field = field;
    }

    @Override
    public List<ValidationResult> validate(JsonObject json) {

        Long aLong = json.getLong(field);

        if (aLong != null) {
            long val = aLong;
            if (!(min <= val && val <= max)) {
                invalidate(json);
            }
        }

        return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setField(field)
            .setValue(json.getValue(field))
            .setErrorCode(ErrorCodes.INVALID_RANGE_VALIDATION_ERROR.code())
            .setAdditionals(
                new JsonObject()
                    .put("min", min)
                    .put("max", max)
            )
            .createValidationResult();
    }
}
