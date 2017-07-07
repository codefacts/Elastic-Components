package elasta.composer.model.response.builder.impl;

import com.google.common.collect.ImmutableMap;
import elasta.composer.ComposerUtils;
import elasta.composer.StatusCodes;
import elasta.composer.model.response.AuthorizationSuccessModel;
import elasta.composer.model.response.builder.AuthorizationSuccessModelBuilder;
import elasta.pipeline.MessageBundle;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 5/21/2017.
 */
final public class AuthorizationSuccessModelBuilderImpl implements AuthorizationSuccessModelBuilder {
    final String statusCode;
    final MessageBundle messageBundle;

    public AuthorizationSuccessModelBuilderImpl(String statusCode, MessageBundle messageBundle) {
        Objects.requireNonNull(statusCode);
        Objects.requireNonNull(messageBundle);
        this.statusCode = statusCode;
        this.messageBundle = messageBundle;
    }

    @Override
    public JsonObject build(BuildParams params) {
        return new JsonObject(
            ImmutableMap.of(
                AuthorizationSuccessModel.statusCode, StatusCodes.authorizationSuccess,
                AuthorizationSuccessModel.message, messageBundle.translate(
                    statusCode,
                    ComposerUtils.emptyJsonObject()
                )
            )
        );
    }
}
