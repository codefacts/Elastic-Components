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
public class MinFieldCount implements Validator<JsonObject> {
    private final MessageBundle messageBundle;
    private final int minFieldCount;

    public MinFieldCount(MessageBundle messageBundle, int minFieldCount) {
        this.messageBundle = messageBundle;
        this.minFieldCount = minFieldCount;
    }

    @Override
    public ValidationResult validate(JsonObject json) {

        if (json.size() < minFieldCount) {
            return invalidate(json);
        }

        return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setErrorCode(ErrorCodes.LENGTH_VALIDATION_ERROR.code())
            .setAdditionals(
                new JsonObject()
                    .put("minFieldCount", minFieldCount))
            .createValidationResult();
    }
}
