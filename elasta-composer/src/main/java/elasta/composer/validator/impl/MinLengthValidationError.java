package elasta.composer.validator.impl;

import elasta.composer.util.ErrorCodes;
import elasta.composer.util.MessageBundle;
import elasta.composer.validator.ValidationResult;
import elasta.composer.validator.ValidationResultBuilder;
import elasta.composer.validator.Validator;
import io.vertx.core.json.JsonObject;

import static java.util.Objects.requireNonNull;

/**
 * Created by shahadat on 2/28/16.
 */
public class MinLengthValidationError implements Validator<JsonObject> {
    private final MessageBundle messageBundle;
    private final String field;
    private final int minLength;

    public MinLengthValidationError(MessageBundle messageBundle, String field, int minLength) {
        this.minLength = minLength;
        requireNonNull(messageBundle);
        requireNonNull(field);
        this.messageBundle = messageBundle;
        this.field = field;
    }

    @Override
    public ValidationResult validate(JsonObject json) {

        String string = json.getString(field);

        if (string != null) {
            if (!(string.length() >= minLength)) {
                return invalidate(json);
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
