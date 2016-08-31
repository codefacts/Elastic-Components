package elasta.composer.validator.impl.type;

import io.crm.MessageBundle;
import io.crm.validator.ValidationResult;
import io.crm.validator.Validator;
import io.vertx.core.json.JsonObject;

import static io.crm.util.Util.as;

/**
 * Created by shahadat on 2/28/16.
 */
public class FloatValidator implements Validator<JsonObject> {
    private final TypeValidator typeValidator;

    public FloatValidator(MessageBundle messageBundle, String field) {
        typeValidator = new TypeValidator(messageBundle, field,
            jsonObject -> as(jsonObject.getValue(field), Float.class),
            validationResult -> {
                validationResult.getAdditionals()
                    .put(TypeValidator.TYPE, Types.FLOAT);
                return validationResult;
            });
    }


    @Override
    public ValidationResult validate(JsonObject val) {
        return typeValidator.validate(val);
    }
}
