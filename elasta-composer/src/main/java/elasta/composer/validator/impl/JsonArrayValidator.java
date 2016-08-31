package elasta.composer.validator.impl;

import io.crm.MessageBundle;
import io.crm.validator.ValidationResult;
import io.crm.validator.Validator;
import io.crm.validator.impl.type.TypeValidator;
import io.crm.validator.impl.type.Types;
import io.vertx.core.json.JsonObject;

/**
 * Created by shahadat on 3/1/16.
 */
public class JsonArrayValidator implements Validator<JsonObject> {
    private final TypeValidator typeValidator;

    public JsonArrayValidator(MessageBundle messageBundle, String field) {
        typeValidator = new TypeValidator(messageBundle, field,
            jsonObject -> jsonObject.getJsonArray(field),
            validationResult -> {
                validationResult.getAdditionals()
                    .put(TypeValidator.TYPE, Types.JSON_ARRAY);
                return validationResult;
            });
    }


    @Override
    public ValidationResult validate(JsonObject val) {
        return typeValidator.validate(val);
    }
}
