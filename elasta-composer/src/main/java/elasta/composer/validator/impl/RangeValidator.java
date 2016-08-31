package elasta.composer.validator.impl;

import io.crm.ErrorCodes;
import io.crm.MessageBundle;
import io.crm.validator.ValidationResult;
import io.crm.validator.ValidationResultBuilder;
import io.crm.validator.Validator;
import io.vertx.core.json.JsonObject;

import static java.util.Objects.requireNonNull;

/**
 * Created by shahadat on 2/28/16.
 */
public class RangeValidator implements Validator<JsonObject> {
    private final MessageBundle messageBundle;
    private final String field;
    private final long min, max;

    public RangeValidator(MessageBundle messageBundle, String field, long min, long max) {
        this.min = min;
        this.max = max;
        requireNonNull(messageBundle);
        requireNonNull(field);
        this.messageBundle = messageBundle;
        this.field = field;
    }

    @Override
    public ValidationResult validate(JsonObject json) {

        Long aLong = json.getLong(field);

        if (aLong != null) {
            long val = aLong;
            if (!(min <= val && val <= max)) {
                invalidate(json);
            }
        }

        return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setField(field)
            .setValue(json.getValue(field))
            .setErrorCode(ErrorCodes.INVALID_RANGE_VALIDATION_ERROR.code())
            .setAdditionals(
                new JsonObject()
                    .put("min", min)
                    .put("max", max)
            )
            .createValidationResult();
    }
}
