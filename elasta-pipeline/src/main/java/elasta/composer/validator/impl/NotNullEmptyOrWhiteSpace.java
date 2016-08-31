package elasta.composer.validator.impl;

import elasta.composer.util.ErrorCodes;
import elasta.composer.util.MessageBundle;
import elasta.composer.validator.ValidationResult;
import elasta.composer.validator.ValidationResultBuilder;
import elasta.composer.validator.Validator;
import io.vertx.core.json.JsonObject;

/**
 * Created by shahadat on 2/28/16.
 */
public class NotNullEmptyOrWhiteSpace implements Validator<JsonObject> {
    private final MessageBundle messageBundle;
    private final String field;

    public NotNullEmptyOrWhiteSpace(MessageBundle messageBundle, String field) {
        this.messageBundle = messageBundle;
        this.field = field;
    }

    @Override
    public ValidationResult validate(JsonObject json) {
        String string = json.getString(field);
        if (string == null)
            return invalidate(json);
        else if (string.trim().isEmpty())
            return invalidate(json);
        else return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setField(field)
            .setValue(json.getValue(field))
            .setErrorCode(ErrorCodes.INVALID_VALUE_VALIDATION_ERROR.code())
            .createValidationResult();
    }
}
