package elasta.composer.pipeline.validator.impl.type;

import elasta.composer.pipeline.Types;
import elasta.composer.pipeline.validator.JsonObjectValidator;
import elasta.composer.pipeline.validator.ValidationResult;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by shahadat on 2/28/16.
 */
public class StringValidator implements JsonObjectValidator {
    private final TypeValidator typeValidator;

    public StringValidator(String field) {
        typeValidator = new TypeValidator(field,
            jsonObject -> jsonObject.getString(field),
            validationResult -> {
                validationResult.getAdditionals()
                    .put(TypeValidator.TYPE, Types.STRING);
                return validationResult;
            });
    }


    @Override
    public List<ValidationResult> validate(JsonObject val) {
        return typeValidator.validate(val);
    }
}
