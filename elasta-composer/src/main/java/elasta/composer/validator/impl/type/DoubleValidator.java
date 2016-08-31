package elasta.composer.validator.impl.type;

import elasta.composer.util.MessageBundle;
import elasta.composer.util.Util;
import elasta.composer.validator.ValidationResult;
import elasta.composer.validator.Validator;
import io.vertx.core.json.JsonObject;

/**
 * Created by shahadat on 2/28/16.
 */
public class DoubleValidator implements Validator<JsonObject> {
    private final TypeValidator typeValidator;

    public DoubleValidator(MessageBundle messageBundle, String field) {
        typeValidator = new TypeValidator(messageBundle, field,
            jsonObject -> Util.as(jsonObject.getValue(field), Double.class),
            validationResult -> {
                validationResult.getAdditionals()
                    .put(TypeValidator.TYPE, Types.DOUBLE);
                return validationResult;
            });
    }


    @Override
    public ValidationResult validate(JsonObject val) {
        return typeValidator.validate(val);
    }
}