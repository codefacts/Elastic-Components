package elasta.pipeline.validator.impl;

import com.google.common.collect.ImmutableList;
import elasta.pipeline.util.ErrorCodes;
import elasta.pipeline.validator.JsonObjectValidator;
import elasta.pipeline.validator.ValidationResult;
import elasta.pipeline.validator.ValidationResultBuilder;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by shahadat on 2/28/16.
 */
public class NotNullEmptyOrWhiteSpaceValidator implements JsonObjectValidator {
    private final String field;

    public NotNullEmptyOrWhiteSpaceValidator(String field) {
        this.field = field;
    }

    @Override
    public List<ValidationResult> validate(JsonObject json) {
        String string = json.getString(field);
        if (string == null)
            return ImmutableList.of(invalidate(json));
        else if (string.trim().isEmpty())
            return ImmutableList.of(invalidate(json));
        else return ImmutableList.of();
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setField(field)
            .setValue(json.getValue(field))
            .setErrorCode(ErrorCodes.INVALID_VALUE_VALIDATION_ERROR.code())
            .createValidationResult();
    }
}
