package elasta.composer.pipeline.validator.impl;

import com.google.common.collect.ImmutableList;
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
public class LengthValidator implements JsonObjectValidator {
    private final String field;
    private final int minLength;
    private final int maxLength;

    public LengthValidator(String field, int minLength, int maxLength) {
        this.minLength = minLength;
        this.maxLength = maxLength;
        requireNonNull(field);
        this.field = field;
    }

    @Override
    public List<ValidationResult> validate(JsonObject json) {

        String string = json.getString(field);

        if (string != null) {
            if (!(minLength <= string.length() && string.length() <= maxLength)) {
                return ImmutableList.of(invalidate(json));
            }
        }

        return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setField(field)
            .setValue(json.getValue(field))
            .setErrorCode(ErrorCodes.LENGTH_VALIDATION_ERROR.code())
            .setAdditionals(
                new JsonObject()
                    .put("minLength", minLength)
                    .put("maxLength", maxLength)
            )
            .createValidationResult();
    }
}
