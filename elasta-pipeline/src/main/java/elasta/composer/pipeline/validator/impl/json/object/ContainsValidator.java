package elasta.composer.pipeline.validator.impl.json.object;

import com.google.common.collect.ImmutableList;
import elasta.composer.pipeline.util.ErrorCodes;
import elasta.composer.pipeline.validator.JsonObjectValidator;
import elasta.composer.pipeline.validator.ValidationResult;
import elasta.composer.pipeline.validator.ValidationResultBuilder;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by shahadat on 3/1/16.
 */
public class ContainsValidator implements JsonObjectValidator {
    private final String field;

    public ContainsValidator(String field) {
        this.field = field;
    }

    @Override
    public List<ValidationResult> validate(JsonObject json) {

        if (!json.containsKey(field)) {
            return ImmutableList.of(invalidate(json));
        }
        return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setErrorCode(ErrorCodes.LENGTH_VALIDATION_ERROR.code())
            .setAdditionals(
                new JsonObject()
                    .put("field", field))
            .createValidationResult();
    }
}
