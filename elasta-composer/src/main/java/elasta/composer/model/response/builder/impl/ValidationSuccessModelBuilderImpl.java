package elasta.composer.model.response.builder.impl;

import com.google.common.collect.ImmutableMap;
import elasta.composer.ComposerUtils;
import elasta.composer.ResponseCodes;
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
                ValidationSuccessModel.statusCode, ResponseCodes.validateSuccess,
                ValidationSuccessModel.message, messageBundle.translate(ResponseCodes.validateSuccess, ComposerUtils.emptyJsonObject())
            )
        );
    }
}
