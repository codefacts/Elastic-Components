package elasta.composer.validator;

import elasta.composer.util.ErrorCodes;
import elasta.composer.util.MessageBundle;
import io.vertx.core.json.JsonObject;

import static java.util.Objects.requireNonNull;

/**
 * Created by shahadat on 4/3/16.
 */
public class PositiveValidator implements Validator<JsonObject> {
    private final MessageBundle messageBundle;
    private final String field;

    public PositiveValidator(MessageBundle messageBundle, String field) {
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
            if (val < 0) {
                invalidate(json);
            }
        }

        return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setField(field)
            .setValue(json.getValue(field))
            .setErrorCode(ErrorCodes.POSITIVE_NUMBER_VALIDATION_ERROR.code())
            .createValidationResult();
    }
}
