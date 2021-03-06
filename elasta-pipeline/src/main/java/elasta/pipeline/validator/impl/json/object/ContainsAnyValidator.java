package elasta.pipeline.validator.impl.json.object;

import com.google.common.collect.ImmutableList;
import elasta.pipeline.util.ErrorCodes;
import elasta.pipeline.validator.JsonObjectValidator;
import elasta.pipeline.validator.ValidationResult;
import elasta.pipeline.validator.ValidationResultBuilder;
import io.vertx.core.json.JsonObject;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by shahadat on 3/1/16.
 */
public class ContainsAnyValidator implements JsonObjectValidator {
    private final Set<String> fields;

    public ContainsAnyValidator(Set<String> fields) {
        this.fields = fields;
    }

    @Override
    public List<ValidationResult> validate(JsonObject json) {

        if (Collections.disjoint(json.fieldNames(), fields)) {
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
