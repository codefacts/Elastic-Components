package elasta.composer.model.response.builder.impl;

import com.google.common.collect.ImmutableMap;
import elasta.composer.model.response.ValidationErrorModel;
import elasta.composer.model.response.ValidationResultModel;
import elasta.composer.model.response.builder.ValidationResultModelBuilder;
import elasta.pipeline.MessageBundle;
import elasta.pipeline.validator.ValidationResult;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 5/14/2017.
 */
final public class ValidationResultModelBuilderImpl implements ValidationResultModelBuilder {
    final MessageBundle messageBundle;

    public ValidationResultModelBuilderImpl(MessageBundle messageBundle) {
        Objects.requireNonNull(messageBundle);
        this.messageBundle = messageBundle;
    }

    @Override
    public JsonObject build(BuildParams params) {
        ValidationResult validationResult = params.getValidationResult();
        return new JsonObject(
            ImmutableMap.of(
                ValidationResultModel.field, validationResult.getField(),
                ValidationResultModel.validationErrorCode, validationResult.getErrorCode(),
                ValidationResultModel.message, messageBundle.translate(String.valueOf(validationResult.getErrorCode()), new JsonObject(
                    ImmutableMap.of(
                        ValidationResultModel.field, validationResult.getField(),
                        ValidationResultModel.value, validationResult.getValue()
                    )
                ))
            )
        );
    }
}
