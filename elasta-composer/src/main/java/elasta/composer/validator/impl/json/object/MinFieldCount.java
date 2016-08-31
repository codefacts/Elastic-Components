package elasta.composer.validator.impl.json.object;

import io.crm.ErrorCodes;
import io.crm.MessageBundle;
import io.crm.validator.ValidationResult;
import io.crm.validator.ValidationResultBuilder;
import io.crm.validator.Validator;
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
