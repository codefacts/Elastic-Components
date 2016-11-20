package elasta.pipeline.validator.impl;

import com.google.common.collect.ImmutableList;
import elasta.pipeline.util.ErrorCodes;
import elasta.pipeline.validator.JsonObjectValidator;
import elasta.pipeline.validator.ValidationResult;
import elasta.pipeline.validator.ValidationResultBuilder;
import io.vertx.core.json.JsonObject;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Created by shahadat on 2/28/16.
 */
public class MinLengthValidator implements JsonObjectValidator {
    private final String field;
    private final int minLength;

    public MinLengthValidator(String field, int minLength) {
        this.minLength = minLength;
        requireNonNull(field);
        this.field = field;
    }

    @Override
    public List<ValidationResult> validate(JsonObject json) {

        String string = json.getString(field);

        if (string != null) {
            if (!(string.length() >= minLength)) {
                return ImmutableList.of(invalidate(json));
            }
        }

        return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setField(field)
            .setValue(json.getValue(field))
            .setErrorCode(ErrorCodes.MIN_LENGTH_VALIDATION_ERROR.code())
            .setAdditionals(
                new JsonObject()
                    .put("minLength", minLength)
            )
            .createValidationResult();
    }
}
