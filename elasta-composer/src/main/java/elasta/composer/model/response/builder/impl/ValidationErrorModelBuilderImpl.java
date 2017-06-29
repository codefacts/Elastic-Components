package elasta.composer.model.response.builder.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.ComposerCnsts;
import elasta.composer.model.response.ValidationErrorModel;
import elasta.composer.model.response.builder.ValidationErrorModelBuilder;
import elasta.composer.model.response.builder.ValidationResultModelBuilder;
import elasta.pipeline.MessageBundle;
import elasta.pipeline.validator.ValidationResult;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 5/14/2017.
 */
final public class ValidationErrorModelBuilderImpl implements ValidationErrorModelBuilder {
    final String statusCode;
    final MessageBundle messageBundle;
    final ValidationResultModelBuilder validationResultModelBuilder;

    public ValidationErrorModelBuilderImpl(String statusCode, MessageBundle messageBundle, ValidationResultModelBuilder validationResultModelBuilder) {
        Objects.requireNonNull(statusCode);
        Objects.requireNonNull(messageBundle);
        Objects.requireNonNull(validationResultModelBuilder);
        this.statusCode = statusCode;
        this.messageBundle = messageBundle;
        this.validationResultModelBuilder = validationResultModelBuilder;
    }

    @Override
    public JsonObject build(BuildParams params) {
        return new JsonObject(
            ImmutableMap.of(
                ValidationErrorModel.statusCode, statusCode,
                ValidationErrorModel.message, messageBundle.translate(statusCode, new JsonObject(
                    ImmutableMap.of(ComposerCnsts.entity, params.getEntity())
                )),
                ValidationErrorModel.validationErrors, toValidationErrors(params.getValidationErrors())
            )
        );
    }

    private List<JsonObject> toValidationErrors(Collection<ValidationResult> validationErrors) {
        ImmutableList.Builder<JsonObject> listBuilder = ImmutableList.builder();

        validationErrors.forEach(validationResult -> {
            listBuilder.add(
                validationResultModelBuilder.build(
                    ValidationResultModelBuilder.BuildParams.builder()
                        .validationResult(validationResult)
                        .build()
                )
            );
        });

        return listBuilder.build();
    }
}
