package elasta.composer.validator.impl;

import elasta.composer.util.ErrorCodes;
import elasta.composer.util.MessageBundle;
import elasta.composer.validator.ValidationResult;
import elasta.composer.validator.ValidationResultBuilder;
import elasta.composer.validator.Validator;
import io.vertx.core.json.JsonObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * Created by shahadat on 2/28/16.
 */
public class PatternValidationError implements Validator<JsonObject> {
    private final MessageBundle messageBundle;
    private final String field;
    private final Pattern pattern;
    private final int validationErrorCode;

    public PatternValidationError(MessageBundle messageBundle, String field, Pattern pattern) {
        this.pattern = pattern;
        requireNonNull(messageBundle);
        requireNonNull(field);
        this.messageBundle = messageBundle;
        this.field = field;
        validationErrorCode = ErrorCodes.PATTERN_VALIDATION_ERROR.code();
    }

    public PatternValidationError(MessageBundle messageBundle, String field, Pattern pattern, int validationErrorCode) {
        this.pattern = pattern;
        requireNonNull(messageBundle);
        requireNonNull(field);
        this.messageBundle = messageBundle;
        this.field = field;
        this.validationErrorCode = validationErrorCode;
    }

    @Override
    public ValidationResult validate(JsonObject json) {
        String string = json.getString(field);
        if (string != null) {
            Matcher matcher = pattern.matcher(json.getString(field));
            if (!matcher.matches()) {
                return invalidate(json);
            }
        }
        return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setField(field)
            .setValue(json.getValue(field))
            .setErrorCode(validationErrorCode)
            .setAdditionals(
                new JsonObject()
                    .put("pattern", pattern.pattern()))
            .createValidationResult();
    }
}
