package elasta.composer.model.response.builder.impl;

import com.google.common.collect.ImmutableMap;
import elasta.composer.ComposerUtils;
import elasta.composer.StatusCodes;
import elasta.composer.model.response.ValidationSuccessModel;
import elasta.composer.model.response.builder.ValidationSuccessModelBuilder;
import elasta.pipeline.MessageBundle;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 5/21/2017.
 */
final public class ValidationSuccessModelBuilderImpl implements ValidationSuccessModelBuilder {
    final MessageBundle messageBundle;

    public ValidationSuccessModelBuilderImpl(MessageBundle messageBundle) {
        Objects.requireNonNull(messageBundle);
        this.messageBundle = messageBundle;
    }

    @Override
    public JsonObject build(BuildParams params) {
        return new JsonObject(
            ImmutableMap.of(
                ValidationSuccessModel.statusCode, StatusCodes.validationSuccess,
                ValidationSuccessModel.message, messageBundle.translate(StatusCodes.validationSuccess, ComposerUtils.emptyJsonObject())
            )
        );
    }
}
