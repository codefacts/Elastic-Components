package elasta.composer.validator.impl.json.object;

import elasta.composer.util.ErrorCodes;
import elasta.composer.util.MessageBundle;
import elasta.composer.validator.ValidationResult;
import elasta.composer.validator.ValidationResultBuilder;
import elasta.composer.validator.Validator;
import io.vertx.core.json.JsonObject;

import java.util.Collections;
import java.util.Set;

/**
 * Created by shahadat on 3/1/16.
 */
public class ContainsAny implements Validator<JsonObject> {
    private final MessageBundle messageBundle;
    private final Set<String> fields;

    public ContainsAny(MessageBundle messageBundle, Set<String> fields) {
        this.messageBundle = messageBundle;
        this.fields = fields;
    }

    @Override
    public ValidationResult validate(JsonObject json) {

        if (Collections.disjoint(json.fieldNames(), fields)) {
            return invalidate(json);
        }
        return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setErrorCode(ErrorCodes.LENGTH_VALIDATION_ERROR.code())
            .setAdditionals(
                new JsonObject()
                    .put("fields", fields))
            .createValidationResult();
    }
}
