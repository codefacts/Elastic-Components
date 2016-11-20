package elasta.composer.pipeline.validator.impl;

import elasta.composer.pipeline.util.ErrorCodes;
import elasta.composer.pipeline.validator.JsonObjectValidator;
import elasta.composer.pipeline.validator.ValidationResult;
import elasta.composer.pipeline.validator.ValidationResultBuilder;
import io.vertx.core.json.JsonObject;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Created by shahadat on 4/3/16.
 */
public class PositiveValidator implements JsonObjectValidator {
    private final String field;

    public PositiveValidator(String field) {
        requireNonNull(field);
        this.field = field;
    }

    @Override
    public List<ValidationResult> validate(JsonObject json) {

        Long aLong = json.getLong(field);

        if (aLong != null) {
            long val = aLong;
            if (val < 0) {
                invalidate(json);
            }
        }

        return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setField(field)
            .setValue(json.getValue(field))
            .setErrorCode(ErrorCodes.POSITIVE_NUMBER_VALIDATION_ERROR.code())
            .createValidationResult();
    }
}
