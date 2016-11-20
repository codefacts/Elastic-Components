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
public class MaxLengthValidator implements JsonObjectValidator {
    private final String field;
    private final int maxLength;

    public MaxLengthValidator(String field, int maxLength) {
        this.maxLength = maxLength;
        requireNonNull(field);
        this.field = field;
    }

    @Override
    public List<ValidationResult> validate(JsonObject json) {

        String string = json.getString(field);
        if (string != null) {
            if (!(string.length() <= maxLength)) {
                invalidate(json);
            }
        }

        return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setField(field)
            .setValue(json.getValue(field))
            .setErrorCode(ErrorCodes.MAX_LENGTH_VALIDATION_ERROR.code())
            .setAdditionals(
                new JsonObject()
                    .put("maxLength", maxLength)
            )
            .createValidationResult();
    }
}
