package elasta.composer.validator.impl;

import elasta.composer.util.MessageBundle;
import elasta.composer.validator.ValidationResult;
import elasta.composer.validator.Validator;
import elasta.composer.validator.impl.type.TypeValidator;
import elasta.composer.validator.impl.type.Types;
import io.vertx.core.json.JsonObject;

/**
 * Created by shahadat on 3/1/16.
 */
public class JsonObjectValidator implements Validator<JsonObject> {
    private final TypeValidator typeValidator;

    public JsonObjectValidator(MessageBundle messageBundle, String field) {
        typeValidator = new TypeValidator(messageBundle, field,
            jsonObject -> jsonObject.getInteger(field),
            validationResult -> {
                validationResult.getAdditionals()
                    .put(TypeValidator.TYPE, Types.JSON_OBJECT);
                return validationResult;
            });
    }

    @Override
    public ValidationResult validate(JsonObject val) {
        return typeValidator.validate(val);
    }
}
