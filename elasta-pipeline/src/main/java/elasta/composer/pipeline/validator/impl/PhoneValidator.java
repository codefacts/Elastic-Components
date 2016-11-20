package elasta.composer.pipeline.validator.impl;

import elasta.composer.pipeline.util.ErrorCodes;
import elasta.composer.pipeline.validator.JsonObjectValidator;
import elasta.composer.pipeline.validator.ValidationResult;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by shahadat on 2/28/16.
 */
public class PhoneValidator implements JsonObjectValidator {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+?88)?\\s*(\\d{5}\\s*\\d{6})" +
        "|(\\d{3}\\s*\\d{3}\\s*\\d{5})$", Pattern.CASE_INSENSITIVE);
    private final PatternValidator patternValidator;

    public PhoneValidator(String field) {
        this.patternValidator = new PatternValidator(field,
            PHONE_PATTERN, ErrorCodes.INVALID_PHONE_VALIDATION_ERROR.code());
    }

    @Override
    public List<ValidationResult> validate(JsonObject val) {
        return patternValidator.validate(val);
    }
}
