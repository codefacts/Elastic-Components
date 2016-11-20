package elasta.pipeline.validator.impl;

import com.google.common.collect.ImmutableList;
import elasta.pipeline.util.ErrorCodes;
import elasta.pipeline.validator.JsonObjectValidator;
import elasta.pipeline.validator.ValidationResult;
import elasta.pipeline.validator.ValidationResultBuilder;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * Created by shahadat on 2/28/16.
 */
public class PatternValidator implements JsonObjectValidator {
    private final String field;
    private final Pattern pattern;
    private final int validationErrorCode;

    public PatternValidator(String field, Pattern pattern) {
        this.pattern = pattern;
        requireNonNull(field);
        this.field = field;
        validationErrorCode = ErrorCodes.PATTERN_VALIDATION_ERROR.code();
    }

    public PatternValidator(String field, Pattern pattern, int validationErrorCode) {
        this.pattern = pattern;
        requireNonNull(field);
        this.field = field;
        this.validationErrorCode = validationErrorCode;
    }

    @Override
    public List<ValidationResult> validate(JsonObject json) {
        String string = json.getString(field);
        if (string != null) {
            Matcher matcher = pattern.matcher(json.getString(field));
            if (!matcher.matches()) {
                return ImmutableList.of(invalidate(json));
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
