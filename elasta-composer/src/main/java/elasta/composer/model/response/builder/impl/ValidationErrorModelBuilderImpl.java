package elasta.composer.model.response.builder.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.Cnsts;
import elasta.composer.ComposerUtils;
import elasta.composer.model.response.ValidationErrorModel;
import elasta.composer.model.response.builder.ValidationErrorModelBuilder;
import elasta.pipeline.MessageBundle;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 5/14/2017.
 */
final public class ValidationErrorModelBuilderImpl implements ValidationErrorModelBuilder {
    final String statusCode;
    final MessageBundle messageBundle;

    public ValidationErrorModelBuilderImpl(String statusCode, MessageBundle messageBundle) {
        Objects.requireNonNull(statusCode);
        Objects.requireNonNull(messageBundle);
        this.statusCode = statusCode;
        this.messageBundle = messageBundle;
    }

    @Override
    public JsonObject build(BuildParams params) {
        return new JsonObject(
            ImmutableMap.of(
                ValidationErrorModel.statusCode, statusCode,
                ValidationErrorModel.message, messageBundle.translate(statusCode, new JsonObject(
                    ImmutableMap.of(Cnsts.entity, params.getEntity())
                )),
                ValidationErrorModel.validationErrors, params.getValidationErrors()
            )
        );
    }
}
