package elasta.pipeline.validator.impl;

import com.google.common.collect.ImmutableList;
import elasta.pipeline.validator.JsonObjectValidator;
import elasta.pipeline.validator.ValidationResult;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by Jango on 2016-11-20.
 */
public class JsonObjectValidationPipeline implements JsonObjectValidator {

    private final List<JsonObjectValidator> validatorList;

    public JsonObjectValidationPipeline(List<JsonObjectValidator> validatorList) {
        this.validatorList = validatorList;
    }

    public List<ValidationResult> validate(JsonObject obj) {
        final ImmutableList.Builder<ValidationResult> builder = ImmutableList.builder();

        validatorList.forEach(tValidator -> {
            final List<ValidationResult> result = tValidator.validate(obj);
            if (result != null) builder.addAll(result);
        });

        final ImmutableList<ValidationResult> list = builder.build();
        return list.size() <= 0 ? null : list;
    }
}
