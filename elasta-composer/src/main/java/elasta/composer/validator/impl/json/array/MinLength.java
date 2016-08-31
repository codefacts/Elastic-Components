package elasta.composer.validator.impl.json.array;

import elasta.composer.util.ErrorCodes;
import elasta.composer.util.MessageBundle;
import elasta.composer.validator.ValidationResult;
import elasta.composer.validator.ValidationResultBuilder;
import elasta.composer.validator.Validator;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import static java.util.Objects.requireNonNull;

/**
 * Created by shahadat on 3/1/16.
 */
public class MinLength implements Validator<JsonArray> {
    private final MessageBundle messageBundle;
    private final int minLength;

    public MinLength(MessageBundle messageBundle, int minLength) {
        this.minLength = minLength;
        requireNonNull(messageBundle);
        this.messageBundle = messageBundle;
    }

    @Override
    public ValidationResult validate(JsonArray json) {

        if (json.size() < minLength) {
            return invalidate(json);
        }

        return null;
    }

    private ValidationResult invalidate(JsonArray json) {
        return new ValidationResultBuilder()
            .setErrorCode(ErrorCodes.MAX_LENGTH_VALIDATION_ERROR.code())
            .setAdditionals(
                new JsonObject()
                    .put("maxLength", minLength)
            )
            .createValidationResult();
    }
}
