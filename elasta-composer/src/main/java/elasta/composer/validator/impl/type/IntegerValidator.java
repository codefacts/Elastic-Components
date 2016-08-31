package elasta.composer.validator.impl.type;

import io.crm.MessageBundle;
import io.crm.validator.ValidationResult;
import io.crm.validator.Validator;
import io.vertx.core.json.JsonObject;

import static io.crm.util.Util.as;

/**
 * Created by shahadat on 2/28/16.
 */
public class IntegerValidator implements Validator<JsonObject> {
    private final TypeValidator typeValidator;

    public IntegerValidator(MessageBundle messageBundle, String field) {
        typeValidator = new TypeValidator(messageBundle, field,
            jsonObject -> as(jsonObject.getValue(field), Integer.class),
            validationResult -> {
                validationResult.getAdditionals()
                    .put(TypeValidator.TYPE, Types.INTEGER);
                return validationResult;
            });
    }


    @Override
    public ValidationResult validate(JsonObject val) {
        return typeValidator.validate(val);
    }
}
