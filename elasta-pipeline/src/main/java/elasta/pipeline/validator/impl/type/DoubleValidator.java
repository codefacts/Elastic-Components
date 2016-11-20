package elasta.pipeline.validator.impl.type;

import elasta.pipeline.Types;
import elasta.pipeline.util.Util;
import elasta.pipeline.validator.JsonObjectValidator;
import elasta.pipeline.validator.ValidationResult;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by shahadat on 2/28/16.
 */
public class DoubleValidator implements JsonObjectValidator {
    private final TypeValidator typeValidator;

    public DoubleValidator(String field) {
        typeValidator = new TypeValidator(field,
            jsonObject -> Util.as(jsonObject.getValue(field)),
            validationResult -> {
                validationResult.getAdditionals()
                    .put(TypeValidator.TYPE, Types.DOUBLE);
                return validationResult;
            });
    }


    @Override
    public List<ValidationResult> validate(JsonObject val) {
        return typeValidator.validate(val);
    }
}
