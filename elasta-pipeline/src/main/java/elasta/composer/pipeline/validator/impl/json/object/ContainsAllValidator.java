package elasta.composer.pipeline.validator.impl.json.object;

import com.google.common.collect.ImmutableList;
import elasta.composer.pipeline.util.ErrorCodes;
import elasta.composer.pipeline.validator.JsonObjectValidator;
import elasta.composer.pipeline.validator.ValidationResult;
import elasta.composer.pipeline.validator.ValidationResultBuilder;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Set;

/**
 * Created by shahadat on 3/1/16.
 */
public class ContainsAllValidator implements JsonObjectValidator {
    private final Set<String> fields;

    public ContainsAllValidator(Set<String> fields) {
        this.fields = fields;
    }

    @Override
    public List<ValidationResult> validate(JsonObject json) {

        if (!json.getMap().keySet().containsAll(fields)) {
            return ImmutableList.of(invalidate(json));
        }
        return null;
    }

    private ValidationResult invalidate(JsonObject json) {
        return new ValidationResultBuilder()
            .setErrorCode(ErrorCodes.LENGTH_VALIDATION_ERROR.code())
            .setAdditionals(
                new JsonObject()
                    .put("fields", fields))
            .createValidationResult();
    }
}
