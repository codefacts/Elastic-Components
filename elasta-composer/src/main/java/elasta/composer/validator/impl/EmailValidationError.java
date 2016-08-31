package elasta.composer.validator.impl;

import elasta.composer.util.ErrorCodes;
import elasta.composer.util.MessageBundle;
import elasta.composer.validator.ValidationResult;
import elasta.composer.validator.Validator;
import io.vertx.core.json.JsonObject;

import java.util.regex.Pattern;

/**
 * Created by shahadat on 2/28/16.
 */
public class EmailValidationError implements Validator<JsonObject> {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private final PatternValidationError patternValidationError;

    public EmailValidationError(MessageBundle messageBundle, String field) {
        this.patternValidationError = new PatternValidationError(messageBundle, field, EMAIL_PATTERN, ErrorCodes.INVALID_EMAIL_VALIDATION_ERROR.code());
    }

    @Override
    public ValidationResult validate(JsonObject val) {
        return patternValidationError.validate(val);
    }
}
