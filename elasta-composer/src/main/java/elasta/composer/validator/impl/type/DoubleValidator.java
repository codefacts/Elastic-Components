package elasta.composer.validator.impl.type;

import io.crm.MessageBundle;
import io.crm.validator.ValidationResult;
import io.crm.validator.Validator;
import io.vertx.core.json.JsonObject;

import static io.crm.util.Util.as;

/**
 * Created by shahadat on 2/28/16.
 */
public class DoubleValidator implements Validator<JsonObject> {
    private final TypeValidator typeValidator;

    public DoubleValidator(MessageBundle messageBundle, String field) {
        typeValidator = new TypeValidator(messageBundle, field,
            jsonObject -> as(jsonObject.getValue(field), Double.class),
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
