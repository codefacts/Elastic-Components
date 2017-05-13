package elasta.composer.model.response.builder.impl;

import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.composer.ComposerUtils;
import elasta.composer.model.response.AuthorizationErrorModel;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.pipeline.MessageBundle;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 5/14/2017.
 */
final public class AuthorizationErrorModelBuilderImpl implements AuthorizationErrorModelBuilder {
    final String statusCode;
    final MessageBundle messageBundle;

    public AuthorizationErrorModelBuilderImpl(String statusCode, MessageBundle messageBundle) {
        Objects.requireNonNull(statusCode);
        Objects.requireNonNull(messageBundle);
        this.statusCode = statusCode;
        this.messageBundle = messageBundle;
    }

    @Override
    public JsonObject build(BuildParams params) {

        return new JsonObject(
            ImmutableMap.of(
                AuthorizationErrorModel.statusCode, statusCode,
                AuthorizationErrorModel.message, messageBundle.translate(statusCode, ComposerUtils.emptyJsonObject())
            )
        );
    }
}
