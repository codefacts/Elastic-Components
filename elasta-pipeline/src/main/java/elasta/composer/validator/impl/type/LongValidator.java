package elasta.composer.validator.impl.type;

import elasta.composer.util.MessageBundle;
import elasta.composer.util.Util;
import elasta.composer.validator.ValidationResult;
import elasta.composer.validator.Validator;
import io.vertx.core.json.JsonObject;

/**
 * Created by shahadat on 2/28/16.
 */
public class LongValidator implements Validator<JsonObject> {
    private final TypeValidator typeValidator;

    public LongValidator(MessageBundle messageBundle, String field) {
        typeValidator = new TypeValidator(messageBundle, field,
            jsonObject -> Util.as(jsonObject.getValue(field), Long.class),
            validationResult -> {
                validationResult.getAdditionals()
                    .put(TypeValidator.TYPE, Types.LONG);
                return validationResult;
            });
    }


    @Override
    public ValidationResult validate(JsonObject val) {
        return typeValidator.validate(val);
    }
}
