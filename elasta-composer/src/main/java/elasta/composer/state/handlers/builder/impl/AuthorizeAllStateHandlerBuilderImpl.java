package elasta.composer.state.handlers.builder.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.authorization.Authorizer;
import elasta.commons.Utils;
import elasta.composer.ComposerCnsts;
import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.model.response.builder.AuthorizationSuccessModelBuilder;
import elasta.composer.state.handlers.builder.AuthorizeAllStateHandlerBuilder;
import elasta.composer.state.handlers.impl.AuthorizeAllStateHandlerImpl;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 5/20/2017.
 */
final public class AuthorizeAllStateHandlerBuilderImpl implements AuthorizeAllStateHandlerBuilder {
    final Authorizer authorizer;
    final String action;
    final AuthorizationErrorModelBuilder authorizationErrorModelBuilder;
    final AuthorizationSuccessModelBuilder authorizationSuccessModelBuilder;

    public AuthorizeAllStateHandlerBuilderImpl(Authorizer authorizer, String action, AuthorizationErrorModelBuilder authorizationErrorModelBuilder, AuthorizationSuccessModelBuilder authorizationSuccessModelBuilder) {
        Objects.requireNonNull(authorizer);
        Objects.requireNonNull(action);
        Objects.requireNonNull(authorizationErrorModelBuilder);
        Objects.requireNonNull(authorizationSuccessModelBuilder);
        this.authorizer = authorizer;
        this.action = action;
        this.authorizationErrorModelBuilder = authorizationErrorModelBuilder;
        this.authorizationSuccessModelBuilder = authorizationSuccessModelBuilder;
    }

    @Override
    public MsgEnterEventHandlerP<JsonArray, Json> build() {
        return new AuthorizeAllStateHandlerImpl(
            authorizer, action, authorizationErrorModelBuilder, authorizationSuccessModelBuilder
        );
    }
}
