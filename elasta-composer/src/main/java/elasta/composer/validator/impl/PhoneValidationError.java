package elasta.composer.validator.impl;

import io.crm.ErrorCodes;
import io.crm.MessageBundle;
import io.crm.validator.ValidationResult;
import io.crm.validator.Validator;
import io.vertx.core.json.JsonObject;

import java.util.regex.Pattern;

/**
 * Created by shahadat on 2/28/16.
 */
public class PhoneValidationError implements Validator<JsonObject> {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+?88)?\\s*(\\d{5}\\s*\\d{6})" +
        "|(\\d{3}\\s*\\d{3}\\s*\\d{5})$", Pattern.CASE_INSENSITIVE);
    private final PatternValidationError patternValidationError;

    public PhoneValidationError(MessageBundle messageBundle, String field) {
        this.patternValidationError = new PatternValidationError(messageBundle, field,
            PHONE_PATTERN, ErrorCodes.INVALID_PHONE_VALIDATION_ERROR.code());
    }

    @Override
    public ValidationResult validate(JsonObject val) {
        return patternValidationError.validate(val);
    }
}
