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
public class Contains implements Validator<JsonObject> {
    private final MessageBundle messageBundle;
    private final String field;

    public Contains(MessageBundle messageBundle, String field) {
        this.messageBundle = messageBundle;
        this.field = field;
    }

    @Override
    public ValidationResult validate(JsonObject json) {

        if (!json.containsKey(field)) {
            return invalidate(json);
        }
        return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setErrorCode(ErrorCodes.LENGTH_VALIDATION_ERROR.code())
            .setAdditionals(
                new JsonObject()
                    .put("field", field))
            .createValidationResult();
    }
}
