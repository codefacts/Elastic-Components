package elasta.composer.validator.impl.json.object;

import elasta.composer.util.ErrorCodes;
import elasta.composer.util.MessageBundle;
import elasta.composer.validator.ValidationResult;
import elasta.composer.validator.ValidationResultBuilder;
import elasta.composer.validator.Validator;
import io.vertx.core.json.JsonObject;

/**
 * Created by shahadat on 3/1/16.
 */
public class MaxFieldCount implements Validator<JsonObject> {
    private final MessageBundle messageBundle;
    private final int maxFieldCount;

    public MaxFieldCount(MessageBundle messageBundle, int maxFieldCount) {
        this.messageBundle = messageBundle;
        this.maxFieldCount = maxFieldCount;
    }

    @Override
    public ValidationResult validate(JsonObject json) {

        if (json.size() > maxFieldCount) {
            return invalidate(json);
        }

        return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setErrorCode(ErrorCodes.LENGTH_VALIDATION_ERROR.code())
            .setAdditionals(
                new JsonObject()
                    .put("maxFieldCount", maxFieldCount))
            .createValidationResult();
    }
}
