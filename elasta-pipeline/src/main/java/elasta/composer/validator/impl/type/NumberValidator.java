package elasta.composer.validator.impl.type;

import elasta.composer.util.MessageBundle;
import elasta.composer.validator.ValidationResult;
import elasta.composer.validator.Validator;
import io.vertx.core.json.JsonObject;

import static elasta.composer.util.Util.as;

/**
 * Created by shahadat on 3/1/16.
 */
public class NumberValidator implements Validator<JsonObject> {
    private final TypeValidator typeValidator;

    public NumberValidator(MessageBundle messageBundle, String field) {
        typeValidator = new TypeValidator(messageBundle, field,
            jsonObject -> as(jsonObject.getValue(field), Number.class),
            validationResult -> {
                validationResult.getAdditionals()
                    .put(TypeValidator.TYPE, Types.NUMBER);
                return validationResult;
            });
    }


    @Override
    public ValidationResult validate(JsonObject val) {
        return typeValidator.validate(val);
    }
}
