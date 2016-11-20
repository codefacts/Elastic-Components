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
public class EmailValidator implements JsonObjectValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private final PatternValidator patternValidator;

    public EmailValidator(String field) {
        this.patternValidator = new PatternValidator(field, EMAIL_PATTERN, ErrorCodes.INVALID_EMAIL_VALIDATION_ERROR.code());
    }

    @Override
    public List<ValidationResult> validate(JsonObject val) {
        return patternValidator.validate(val);
    }
}
