package elasta.pipeline.validator.impl;

import elasta.pipeline.util.ErrorCodes;
import elasta.pipeline.validator.JsonObjectValidator;
import elasta.pipeline.validator.ValidationResult;
import elasta.pipeline.validator.ValidationResultBuilder;
import io.vertx.core.json.JsonObject;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Created by shahadat on 4/3/16.
 */
public class NonZeroValidator implements JsonObjectValidator {
    private final String field;

    public NonZeroValidator(String field) {
        requireNonNull(field);
        this.field = field;
    }

    @Override
    public List<ValidationResult> validate(JsonObject json) {

        Long aLong = json.getLong(field);

        if (aLong != null) {
            long val = aLong;
            if (val == 0) {
                invalidate(json);
            }
        }

        return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setField(field)
            .setValue(json.getValue(field))
            .setErrorCode(ErrorCodes.NON_ZERO_NUMBER_VALIDATION_ERROR.code())
            .createValidationResult();
    }
}
