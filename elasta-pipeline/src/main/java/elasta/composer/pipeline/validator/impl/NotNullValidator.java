package elasta.composer.pipeline.validator.impl;

import com.google.common.collect.ImmutableList;
import elasta.composer.pipeline.util.ErrorCodes;
import elasta.composer.pipeline.validator.JsonObjectValidator;
import elasta.composer.pipeline.validator.ValidationResult;
import elasta.composer.pipeline.validator.ValidationResultBuilder;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by shahadat on 2/28/16.
 */
public class NotNullValidator implements JsonObjectValidator {
    private final String field;

    public NotNullValidator(String field) {
        this.field = field;
    }

    @Override
    public List<ValidationResult> validate(JsonObject json) {
        if (!json.containsKey(field))
            return ImmutableList.of(invalidate(json));
        else return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setField(field)
            .setValue(json.getValue(field))
            .setErrorCode(ErrorCodes.NULL_VALIDATION_ERROR.code())
            .createValidationResult();
    }
}
