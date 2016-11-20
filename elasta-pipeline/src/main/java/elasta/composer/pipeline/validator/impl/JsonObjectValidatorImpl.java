package elasta.composer.pipeline.validator.impl;

import elasta.composer.pipeline.validator.impl.type.TypeValidator;
import elasta.composer.pipeline.validator.ValidationResult;
import elasta.composer.pipeline.validator.Validator;
import elasta.composer.pipeline.Types;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by shahadat on 3/1/16.
 */
public class JsonObjectValidatorImpl implements Validator<JsonObject> {
    private final TypeValidator typeValidator;

    public JsonObjectValidatorImpl(String field) {
        typeValidator = new TypeValidator(field,
            jsonObject -> jsonObject.getInteger(field),
            validationResult -> {
                validationResult.getAdditionals()
                    .put(TypeValidator.TYPE, Types.JSON_OBJECT);
                return validationResult;
            });
    }

    @Override
    public List<ValidationResult> validate(JsonObject val) {
        return typeValidator.validate(val);
    }
}
